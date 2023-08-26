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

/**
 * Dubbo调用远程服务,从数据库中查询接口信息
 * @author Gundam
 */
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
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectOne(queryWrapper);
        System.out.println(interfaceInfo);
        return interfaceInfo;
    }
}
