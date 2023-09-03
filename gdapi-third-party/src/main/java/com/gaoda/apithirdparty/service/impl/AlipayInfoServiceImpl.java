package com.gaoda.apithirdparty.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gaoda.apithirdparty.mapper.AlipayInfoMapper;
import com.gaoda.apithirdparty.model.entity.AlipayInfo;
import com.gaoda.apithirdparty.service.AlipayInfoService;
import org.springframework.stereotype.Service;

@Service
public class AlipayInfoServiceImpl extends ServiceImpl<AlipayInfoMapper, AlipayInfo>
    implements AlipayInfoService {

}