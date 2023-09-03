package com.gundam.gdapi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gundam.gdapi.mapper.InterfaceChargingMapper;
import com.gundam.gdapi.service.InterfaceChargingService;
import com.gundam.gdapicommon.model.entity.InterfaceCharging;
import org.springframework.stereotype.Service;

@Service
public class InterfaceChargingServiceImpl extends ServiceImpl<InterfaceChargingMapper, InterfaceCharging>
    implements InterfaceChargingService {

}