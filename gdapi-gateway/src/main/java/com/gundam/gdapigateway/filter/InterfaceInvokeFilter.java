package com.gundam.gdapigateway.filter;

import com.gundam.gdapiclientsdk.utils.SignUtils;
import com.gundam.gdapicommon.model.entity.InterfaceInfo;
import com.gundam.gdapicommon.model.entity.User;
import com.gundam.gdapicommon.service.ApiBackendService;
import com.gundam.gdapicommon.service.InnerInterfaceInfoService;
import com.gundam.gdapicommon.service.InnerUserInterfaceInfoService;
import com.gundam.gdapicommon.service.InnerUserService;
import com.gundam.gdapicommon.vo.UserInterfaceInfoMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.gundam.gdapicommon.constant.RabbitmqConstant.EXCHANGE_INTERFACE_CONSISTENT;
import static com.gundam.gdapicommon.constant.RabbitmqConstant.ROUTING_KEY_INTERFACE_CONSISTENT;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class    InterfaceInvokeFilter implements GatewayFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;


//    @DubboReference
//    private ApiBackendService apiBackendService;





//    private static final List<String> IP_WHITE_LIST = Arrays.asList("127.0.0.1");

    private static final String INTERFACE_HOST = "http://localhost:8081";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RabbitTemplate rabbitTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 网关接收到用户的请求
        // 2. 输出请求日志
        // 3. 黑白名单
        // 4. 用户鉴权(API签名认证)
        // 5. 远程调用 判断接口是否存在并获取到调用接口信息
        // 6. 判断接口是否还有可调用次数 没有则拒绝
        // 7. 发起接口调用请求
        // 8. 获取响应结果并输出响应日志
        // 9. 令接口调用次数+1


        // ***********************************************************************


        // 1. 接收用户请求
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = INTERFACE_HOST + request.getPath().value();
        String method = request.getMethod().toString();
        String sourceAddress = request.getLocalAddress().getHostString();
        // 2. 请求日志
        log.info("请求ID: " + request.getId());
        log.info("请求URL: " + request.getURI());
        log.info("请求path: " + path);
        log.info("请求方法: " + method);
        log.info("请求参数: " + request.getQueryParams());
        log.info("本地请求地址: " + sourceAddress);
        log.info("远程请求地址: " + request.getRemoteAddress());

        // 3. 黑白名单
//        if (!IP_WHITE_LIST.contains(sourceAddress)) {
//            return handleNoAuth(response);
//        }


        // 4. 用户鉴权(判断ak,sk是否合法)
        // 4.1 获取请求参数
        HttpHeaders headers = request.getHeaders();

        String accessKey = headers.getFirst("accessKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");

        // 4.2 去数据库查询接口是否分配给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e){
            log.error("远程调用获取调用接口的用户的信息失败!", e);
        }
        if (invokeUser == null){
            return handleNoAuth(response);
        }

        // 4.3 从数据库中查出secretKey进行比对
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.getSign(body, secretKey);
        if (sign == null || !sign.equals(serverSign)){
            log.info("签名校验失败!!!");
            return handleNoAuth(response);
        }

        // 4.4 防重放,使用redis存储请求的唯一标识随机事件nonce,并定时淘汰
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(nonce, "1", 5, TimeUnit.MINUTES);
        if (success == null) {
            log.info("随机数存储失败!!!");
            return handleNoAuth(response);
        }

        // 5. 判断请求的模拟接口是否存在?
        // 从数据库中查询模拟接口是否存在,请求方法是否匹配(还可以校验请求参数)
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path, method);
        } catch (Exception e){
            log.error("远程调用获取被调用接口信息失败!", e);
        }
        if (interfaceInfo == null){
            log.error("请求接口不存在!!!");
            return handleNoAuth(response);
        }

        // 6. 判断接口是否还有调用次数 并统计接口调用
        // 将二者转化成原子性操作(backend本地服务的本地事务实现)，实现二者数据一致性
        boolean result = false;
        try {
            result = innerUserInterfaceInfoService.invokeCount(interfaceInfo.getId() ,invokeUser.getId() );
        } catch (Exception e) {
            log.error("统计接口invokeCount失败(可能是用户恶意调用不存在的接口)", e);
            return handleInvokeError(response);
        }
        if (!result) {
            log.error("接口剩余次数不足!");
            return handleNoAuth(response);
        }


//        // 7. 发起接口调用 网关路由实现
//        Mono<Void> filter = chain.filter(exchange);


        return handleResponse(exchange, chain, interfaceInfo.getId(), interfaceInfo.getUserId());

    }

    @Override
    public int getOrder() {
        return -2;
    }

    private Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }

    /**
     * 处理响应
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId){
        try {
            //从交换机拿响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            //缓存数据的工厂,拿到缓存数据
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            //拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if(statusCode == HttpStatus.OK){
                //装饰,增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    //等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //向返回值里写数据
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                byte[] content = new byte[dataBuffer.readableByteCount()];
                                dataBuffer.read(content);
                                DataBufferUtils.release(dataBuffer);//释放掉内存
                                // 8.获取响应结果并构建日志
                                log.info("接口调用响应状态码: " + originalResponse.getStatusCode());
                                String responseBody = new String(content, StandardCharsets.UTF_8);
                                // 9. 接口调用失败,利用消息队列实现接口统计数据的回滚.因为消息队列比远程调用更可靠
                                if (!(originalResponse.getStatusCode() == HttpStatus.OK)) {
                                    log.error("接口异常调用-响应体 : " + responseBody);
                                    UserInterfaceInfoMessage message = new UserInterfaceInfoMessage(userId, interfaceInfoId);
                                    rabbitTemplate.convertAndSend(EXCHANGE_INTERFACE_CONSISTENT, ROUTING_KEY_INTERFACE_CONSISTENT, message);
                                }
                                return bufferFactory.wrap(content);
                            }));
                        } else {
                            //调用失败,返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                //设置response对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            return chain.filter(exchange);//降级处理返回数据
        }catch (Exception e){
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

}



