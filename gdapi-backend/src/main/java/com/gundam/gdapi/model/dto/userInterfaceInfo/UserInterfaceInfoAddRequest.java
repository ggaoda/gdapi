package com.gundam.gdapi.model.dto.userInterfaceInfo;


import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 * @author Gundam
 */
@Data
public class UserInterfaceInfoAddRequest implements Serializable {

    /**
     * 调用用户ID
     */
    private Long userId;

    /**
     * 接口Id
     */
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    private Integer leftNum;


}