package com.gundam.gdapi.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gundam.gdapi.common.ErrorCode;
import com.gundam.gdapi.exception.BusinessException;
import com.gundam.gdapi.mapper.InterfaceInfoMapper;
import com.gundam.gdapicommon.model.entity.InterfaceInfo;
import com.gundam.gdapicommon.service.InnerInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        queryWrapper.eq("method", method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }
}
