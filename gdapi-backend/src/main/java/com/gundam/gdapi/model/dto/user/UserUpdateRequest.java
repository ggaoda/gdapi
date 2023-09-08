package com.gundam.gdapi.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户更新请求(管理员)
 *
 * @author Gundam
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String userAvatar;


    /**
     * 用户角色：user/admin/ban
     */
    private Integer role;

    /**
     * 性别
     */
    private Integer gender;

    private static final long serialVersionUID = 1L;
}