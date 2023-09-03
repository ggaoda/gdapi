package com.gundam.gdapigateway.filter;

import com.google.common.util.concurrent.RateLimiter;
import com.gundam.gdapicommon.common.ErrorCode;
import com.gundam.gdapicommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class LoginGlobalFilter implements GlobalFilter, Ordered {



    @Resource
    private RateLimiter rateLimiter;


    public static final List<String> NOT_LOGIN_PATH = Arrays.asList(
            "/user/register",
            "/user/login",
            "/user/get/login",
            "/interfaceInfo/list/page",
            "/v2/api-docs",
            "/user/interface/**");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();
        System.out.println("请求fliter : " );
        // 限流过滤
        if (!rateLimiter.tryAcquire()) {
            log.error("请求过于频繁,进行限流!!!");
            throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS);
        }


//        return response.setComplete();
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
