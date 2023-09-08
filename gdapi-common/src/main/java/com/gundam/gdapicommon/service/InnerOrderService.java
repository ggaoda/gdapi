package com.gundam.gdapicommon.service;

import com.gundam.gdapicommon.model.entity.Order;

import java.util.List;

public interface InnerOrderService {
    /**
     * 获取前 limit 购买数量的接口
     * @param limit
     * @return
     */
    List<Order> listTopBuyInterfaceInfo(int limit);
}