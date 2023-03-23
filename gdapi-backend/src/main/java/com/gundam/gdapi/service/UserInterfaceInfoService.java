package com.gundam.gdapi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gundam.gdapicommon.model.entity.UserInterfaceInfo;

/**
* @author Gundam
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {



    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);


    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    Boolean invokeCount(long interfaceInfoId, long userId);


}
