package com.gundam.gdapicommon.service;



/**
* @author Gundam
*/
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    Boolean invokeCount(long interfaceInfoId, long userId);

    /**
     * 给指定用户分配接口调用次数
     * @param userId
     * @param interfaceId
     * @param num
     * @return
     */
    boolean updateUserInterfaceInvokeCount(long userId,long interfaceId,int num);


}
