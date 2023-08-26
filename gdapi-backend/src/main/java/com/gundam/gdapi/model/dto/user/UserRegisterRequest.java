package com.gundam.gdapi.model.dto.user;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户注册请求体
 *
 * @author Gundam
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;


    /**
     * 确认密码
     */
    private String checkPassword;

    /**
     * vip编号
     */
    private String vipCode;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 手机验证码
     */
    private String code;

    /**
     * 图形验证码
     */
    private String captcha;
}
