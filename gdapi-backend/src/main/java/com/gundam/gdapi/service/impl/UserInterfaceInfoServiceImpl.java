package com.gundam.gdapi.service.impl;


import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gundam.gdapi.common.ErrorCode;
import com.gundam.gdapi.exception.BusinessException;
import com.gundam.gdapi.mapper.UserInterfaceInfoMapper;
import com.gundam.gdapi.service.UserInterfaceInfoService;
import com.gundam.gdapicommon.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;

/**
* @author Gundam
* @description 针对表【user_interface_info(用户调用接口信息表)】的数据库操作Service实现
* @createDate 2023-03-21 15:33:59
*/
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
    implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {

        if (userInterfaceInfo == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        /**
         * 校验次数
         */
        //创建时,所有参数必须非空
        if (add){
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在!");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0!");
        }



    }

    @Override
    public Boolean invokeCount(long interfaceInfoId, long userId) {

        //校验
        if (interfaceInfoId <= 0 || userId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId",interfaceInfoId);
        updateWrapper.eq("userId",userId);
        updateWrapper.setSql("leftNum = leftNum - 1,totalNum = totalNum + 1");
        return this.update(updateWrapper);
    }

}




