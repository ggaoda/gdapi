package com.gaoda.apiorder.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gaoda.apiorder.model.dto.OrderAddRequest;
import com.gaoda.apiorder.model.dto.OrderQueryRequest;
import com.gaoda.apiorder.service.TOrderService;
import com.gundam.gdapicommon.common.BaseResponse;
import com.gundam.gdapicommon.common.ResultUtils;
import com.gundam.gdapicommon.vo.OrderVO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/")
public class OrderController {

    @Resource
    private TOrderService orderService;


    @PostMapping("/addOrder")
    public BaseResponse<OrderVO> interfaceTOrder(@RequestBody OrderAddRequest orderAddRequest, HttpServletRequest request){
        OrderVO order = orderService.addOrder(orderAddRequest,request);
        return ResultUtils.success(order);
    }


    @GetMapping("/list")
    public BaseResponse<Page<OrderVO>> listPageOrder(OrderQueryRequest orderQueryRequest, HttpServletRequest request){
        Page<OrderVO> orderPage = orderService.listPageOrder(orderQueryRequest, request);
        return ResultUtils.success(orderPage);
    }


}
