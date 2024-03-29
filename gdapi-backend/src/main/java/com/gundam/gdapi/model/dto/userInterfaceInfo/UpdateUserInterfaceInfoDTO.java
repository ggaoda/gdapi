package com.gundam.gdapi.model.dto.userInterfaceInfo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateUserInterfaceInfoDTO implements Serializable {

    private static final long serialVersionUID = 1472097902521779075L;

    private Long userId;

    private Long interfaceId;

    private Long lockNum;
}