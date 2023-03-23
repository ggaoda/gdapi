package com.gundam.gdapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gundam.gdapicommon.model.entity.InterfaceInfo;

/**
* @author Gundam
* @description 针对表【interface_info(接口信息表)】的数据库操作Service
* @createDate 2023-03-09 16:08:25
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

}
