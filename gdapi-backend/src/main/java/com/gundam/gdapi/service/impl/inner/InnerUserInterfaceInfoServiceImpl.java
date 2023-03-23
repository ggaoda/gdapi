package com.gundam.gdapi.service.impl.inner;

import com.gundam.gdapi.service.UserInterfaceInfoService;
import com.gundam.gdapicommon.service.InnerUserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {


    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public Boolean invokeCount(long interfaceInfoId, long userId) {

        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }
}
