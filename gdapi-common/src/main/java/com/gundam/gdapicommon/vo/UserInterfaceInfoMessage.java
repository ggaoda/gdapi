package com.gundam.gdapicommon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInterfaceInfoMessage implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 调用接口ID
     */
    private Long interfaceInfoId;
}
