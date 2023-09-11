package com.gaoda.apiorder.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaoda.apiorder.enums.OrderStatusEnum;
import com.gaoda.apiorder.mapper.TOrderMapper;
import com.gaoda.apiorder.model.dto.OrderAddRequest;
import com.gaoda.apiorder.model.dto.OrderQueryRequest;
import com.gaoda.apiorder.service.TOrderService;
import com.gaoda.apiorder.utils.OrderMqUtils;
import com.google.gson.Gson;
import com.gundam.gdapicommon.common.ErrorCode;
import com.gundam.gdapicommon.constant.UserConstant;
import com.gundam.gdapicommon.exception.BusinessException;
import com.gundam.gdapicommon.model.entity.InterfaceInfo;
import com.gundam.gdapicommon.model.entity.Order;
import com.gundam.gdapicommon.model.entity.User;
import com.gundam.gdapicommon.service.InnerInterfaceInfoService;
import com.gundam.gdapicommon.service.InnerUserInterfaceInfoService;
import com.gundam.gdapicommon.service.InnerUserService;
import com.gundam.gdapicommon.vo.OrderVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.gundam.gdapicommon.constant.CommonConstant.SALT;
import static com.gundam.gdapicommon.constant.UserConstant.LOGIN_USER_KEY;

@Service
public class TOrderServiceImpl extends ServiceImpl<TOrderMapper, Order>
    implements TOrderService {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;



    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private OrderMqUtils orderMqUtils;

    @Resource
    private Gson gson;

    @Resource
    private TOrderMapper orderMapper;


    public static final String USER_LOGIN_STATE = "user:login:";




    @Transactional
    @Override
    public OrderVO addOrder(OrderAddRequest orderAddRequest, HttpServletRequest request) {

//        1.订单服务校验参数，如用户是否存在，接口是否存在等校验

        if (orderAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long userId = orderAddRequest.getUserId();
        Long interfaceId = orderAddRequest.getInterfaceId();
        Double charging = orderAddRequest.getCharging();
        Integer count = orderAddRequest.getCount();
        BigDecimal totalAmount = orderAddRequest.getTotalAmount();


        if (userId == null || interfaceId == null || count ==null || totalAmount == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }



        if (count<=0 || totalAmount.compareTo(new BigDecimal(0)) < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

//        User user = innerUserService.getUserById(userId);
        User user = getLoginUser(request);
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }

        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceById(interfaceId);
        if (interfaceInfo == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口不存在");
        }

        // 后端校验订单总价格
        double temp = charging * count;
        BigDecimal bd = new BigDecimal(temp);
        double finalPrice = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (finalPrice != totalAmount.doubleValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "价格错误");
        }



        // 2.判断接口调用库存是否足够
        int interfaceStock = innerInterfaceInfoService.getInterfaceStockById(interfaceId);
        if (interfaceStock<=0 || interfaceStock - count<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"接口库存不足");
        }

        // 3.扣减接口库存 远程调用实现
        boolean updateStockResult = innerInterfaceInfoService.updateInterfaceStock(interfaceId, count);
        if (!updateStockResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"扣减库存失败");
        }

        //        4.数据库保存订单数据
        Order order = new Order();
        //生成订单号
        String orderNum = generateOrderNum(userId);
        order.setOrderSn(orderNum);
        order.setTotalAmount(orderAddRequest.getTotalAmount().doubleValue());
        BeanUtils.copyProperties(orderAddRequest, order);

        this.save(order);

//        5.同时消息队列发送延时消息
        orderMqUtils.sendOrderSnInfo(order);


        //6.构造订单详情并回显
        OrderVO orderVO = new OrderVO();
        orderVO.setInterfaceId(interfaceId);
        orderVO.setUserId(userId);
        orderVO.setOrderNumber(orderNum);

        orderVO.setTotal(Long.valueOf(count));
        orderVO.setCharging(charging);
        orderVO.setTotalAmount(totalAmount.doubleValue());
        orderVO.setStatus(order.getStatus());
        orderVO.setInterfaceDesc(interfaceInfo.getDescription());
        orderVO.setInterfaceName(interfaceInfo.getName());
        DateTime date = DateUtil.date();
        orderVO.setCreateTime(date);
        orderVO.setExpirationTime(DateUtil.offset(date, DateField.MINUTE, 30));

        return orderVO;
    }

    @Override
    public Page<OrderVO> listPageOrder(OrderQueryRequest orderQueryRequest, HttpServletRequest request) {
        Integer type = Integer.parseInt(orderQueryRequest.getType());
        long current = orderQueryRequest.getCurrent();
        long pageSize = orderQueryRequest.getPageSize();
        if (!OrderStatusEnum.getValues().contains(type)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        User userVO = getLoginUser(request);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userVO.getId()).eq("status",type);
        Page<Order> page = new Page<>(current,pageSize);
        Page<Order> orderPage = this.page(page, queryWrapper);

        Page<OrderVO> orderVOPage = new Page<>(orderPage.getCurrent(),orderPage.getSize(),orderPage.getTotal());

        List<OrderVO> orderVOList = orderPage.getRecords().stream().map(order -> {
            Long interfaceId = order.getInterfaceId();
            InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceById(interfaceId);
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(order, orderVO);
            orderVO.setTotal(Long.valueOf(order.getCount()));
            orderVO.setTotalAmount(order.getTotalAmount());
            orderVO.setOrderNumber(order.getOrderSn());

            orderVO.setInterfaceName(interfaceInfo.getName());
            orderVO.setInterfaceDesc(interfaceInfo.getDescription());
            orderVO.setExpirationTime(DateUtil.offset(order.getCreateTime(), DateField.MINUTE, 30));
            return orderVO;
        }).collect(Collectors.toList());
        orderVOPage.setRecords(orderVOList);
        return orderVOPage;


    }

    @Override
    public List<Order> listTopBuyInterfaceInfo(int limit) {
        return orderMapper.listTopBuyInterfaceInfo(limit);
    }

    /**
     * 生成订单号
     *
     * @return
     */
    private String generateOrderNum(Long userId) {
        String timeId = IdWorker.getTimeId();
        String substring = timeId.substring(0, timeId.length() - 15);
        return substring + RandomUtil.randomNumbers(5) + userId;
    }

    /**
     * 获取登录用户
     * @param request
     * @return
     */
    public User getLoginUser(HttpServletRequest request) {
//        // 先判断是否已登录
//        Long userId =   JwtUtils.getUserIdByToken(request);
//        if (userId == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
//
//        String userJson = stringRedisTemplate.opsForValue().get(USER_LOGIN_STATE + userId);
//        User user = gson.fromJson(userJson, User.class);
//        if (user == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
//
//        return user;
        // 先判断是否已登录

        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw  new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        //从请求头中获取token
        String token = request.getHeader("x-auth-token");
        if (StrUtil.isBlank(token)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        String userToken = SALT + token + currentUser.getId();
        String tokenKey = LOGIN_USER_KEY + userToken;

        String userId = stringRedisTemplate.opsForValue().get(tokenKey);
        if (StrUtil.isEmpty(userId)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }


        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        //long userId = currentUser.getId();

//        currentUser = this.getById(userId);
        Long id = Long.parseLong(userId);
        currentUser = innerUserService.getUserById(id);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
}




