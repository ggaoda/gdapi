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
     * @param interfaceInfoId 接口Id
     * @param userId 用户Id
     * @return 更新数量结果
     */
    Boolean invokeCount(long interfaceInfoId, long userId);


}
