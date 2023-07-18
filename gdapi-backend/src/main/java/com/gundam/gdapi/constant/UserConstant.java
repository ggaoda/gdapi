package com.gundam.gdapi.constant;

/**
 * 用户常量
 * @author Gundam
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色 -- 0
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员角色 -- 1
     */
    int ADMIN_ROLE = 1;

    /**
     * 被封号 -- -1
     */
    int BAN_ROLE = -1;


    /**
     * 登入令牌过期时间
     */

    Long LOGIN_USER_TTL = 10L;

    /**
     * 用户登入key
     */
    String LOGIN_USER_KEY = "api:login:user:";

    // endregion
}
