package com.gundam.gdapi.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gundam.gdapi.common.ErrorCode;
import com.gundam.gdapi.exception.BusinessException;
import com.gundam.gdapi.service.InterfaceInfoService;
import com.gundam.gdapi.mapper.InterfaceInfoMapper;
import com.gundam.gdapicommon.model.entity.InterfaceInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author Gundam
* @description 针对表【interface_info(接口信息表)】的数据库操作Service实现
* @createDate 2023-03-09 16:08:25
*/
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
    implements InterfaceInfoService {

    @Override
    public void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add) {
//     Long id = interfaceInfo.getId();
     String name = interfaceInfo.getName();
//     String description = interfaceInfo.getDescription();
//     String url = interfaceInfo.getUrl();
//     String requestHeader = interfaceInfo.getRequestHeader();
//     String responseHeader = interfaceInfo.getResponseHeader();
//     Integer status = interfaceInfo.getStatus();
//     String method = interfaceInfo.getMethod();
//     Long userId = interfaceInfo.getUserId();
//     Date createTime = interfaceInfo.getCreateTime();
//     Date updateTime = interfaceInfo.getUpdateTime();
//     Integer isDeleted = interfaceInfo.getIsDeleted();

        /**
         * 校验名称
         */
        if (add){
            if (StringUtils.isAnyBlank(name)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        }

        if (StringUtils.isNotBlank(name) && name.length() > 50){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称过长!");
        }



    }

}




