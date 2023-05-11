package com.gundam.gdapi.model.dto.user;

import com.gundam.gdapi.common.PageRequest;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 *
 * @author Gundam
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;


    /**
     * 用户昵称
     */
    private String userName;


    /**
     * 用户角色：user/admin/ban
     */
    private Integer role;

    private static final long serialVersionUID = 1L;
}