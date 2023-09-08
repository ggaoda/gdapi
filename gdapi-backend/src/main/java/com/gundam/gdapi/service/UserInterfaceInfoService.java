package com.gundam.gdapi.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gundam.gdapi.model.dto.userInterfaceInfo.UpdateUserInterfaceInfoDTO;
import com.gundam.gdapi.model.vo.InterfaceInfoVO;
import com.gundam.gdapi.model.vo.UserInterfaceInfoVO;
import com.gundam.gdapicommon.model.entity.UserInterfaceInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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


    List<UserInterfaceInfoVO> getInterfaceInfoByUserId(Long userId, HttpServletRequest request);

    /**
     * 回滚接口调用次数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    boolean recoverInvokeCount(long userId, long interfaceInfoId);

    /**
     * 获取接口的剩余调用次数
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    int getLeftInvokeCount(long userId, long interfaceInfoId);


    /**
     *更新用户接口信息
     * @param updateUserInterfaceInfoDTO
     * @return
     */
    boolean updateUserInterfaceInfo(UpdateUserInterfaceInfoDTO updateUserInterfaceInfoDTO);



    /**
     * 获取调用次数前limit的接口信息
     * @param limit
     * @return
     */
    List<InterfaceInfoVO> interfaceInvokeTopAnalysis(int limit);







}
