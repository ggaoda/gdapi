package com.gundam.gdapi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gundam.gdapi.annotation.AuthCheck;
import com.gundam.gdapi.common.BaseResponse;
import com.gundam.gdapi.common.ErrorCode;
import com.gundam.gdapi.common.ResultUtils;
import com.gundam.gdapi.exception.BusinessException;
import com.gundam.gdapi.mapper.UserInterfaceInfoMapper;
import com.gundam.gdapi.model.vo.InterfaceInfoVO;
import com.gundam.gdapi.service.InterfaceInfoService;
import com.gundam.gdapicommon.model.entity.InterfaceInfo;
import com.gundam.gdapicommon.model.entity.UserInterfaceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分析控制器
 * @author Gundam
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = 1)
    public BaseResponse<List<InterfaceInfoVO>> listTopInvokeInterfaceInfo() {
        // 需要显示调用次数前三的接口信息 --> limit: 3
        List<UserInterfaceInfo> userInterfaceInfoList = userInterfaceInfoMapper.listTopInvokeInterfaceInfo(3);
        // 将查询出来的接口接口放入(Id : UserInterfaceInfo)
        Map<Long, List<UserInterfaceInfo>> interfaceInfoIdObjMap = userInterfaceInfoList.stream()
                .collect(Collectors.groupingBy(UserInterfaceInfo::getInterfaceInfoId));
        //QueryWrapper根据id查询,类型为InterfaceInfo
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", interfaceInfoIdObjMap.keySet());
        //列表查询出来接口
        List<InterfaceInfo> list = interfaceInfoService.list(queryWrapper);
        //相当于 list != null && list.size > 0 列表为空
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        //InterfaceInfoVO封装了接口信息,因为我们这里需要查询总调用次数,那么要新加一个属性totalNum
        //从UserInterfaceInfo里查询出来并赋给它
        List<InterfaceInfoVO> interfaceInfoVOList = list.stream().map(interfaceInfo -> {
            InterfaceInfoVO interfaceInfoVO = new InterfaceInfoVO();
            BeanUtils.copyProperties(interfaceInfo, interfaceInfoVO);
            //              从用户-接口信息中         根据id查出接口(List)   获取首个(因为可能有多个)   获得总调用次数
            int totalNum = interfaceInfoIdObjMap.get(interfaceInfo.getId()).get(0).getTotalNum();
            interfaceInfoVO.setTotalNum(totalNum); //赋给接口VO
            return interfaceInfoVO; //返回数量
        }).collect(Collectors.toList());
        return ResultUtils.success(interfaceInfoVOList); //返回查询结果
    }
}
