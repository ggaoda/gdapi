package com.gaoda.apiorder.provider;

import com.gaoda.apiorder.service.TOrderService;
import com.gundam.gdapicommon.model.entity.Order;
import com.gundam.gdapicommon.service.InnerOrderService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class InnerOrderServiceImpl implements InnerOrderService {
    @Resource
    TOrderService orderService;
    @Override
    public List<Order> listTopBuyInterfaceInfo(int limit) {
        return orderService.listTopBuyInterfaceInfo(limit);
    }
}