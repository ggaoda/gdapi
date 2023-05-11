package com.gundam.gdapicommon.service;


import com.gundam.gdapicommon.model.entity.User;

/**
 * 用户服务
 * @author Gundam
 */
public interface InnerUserService {

    /**
     * 数据库中查询是否已分配给用户密钥
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);

}
