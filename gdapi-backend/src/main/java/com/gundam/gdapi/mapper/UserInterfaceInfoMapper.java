package com.gundam.gdapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gundam.gdapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
* @author Gundam
* @description 针对表【user_interface_info(用户调用接口信息表)】的数据库操作Mapper
* @createDate 2023-03-21 15:33:59
* @Entity generator.domain.UserInterfaceInfo
*/
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {


    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




