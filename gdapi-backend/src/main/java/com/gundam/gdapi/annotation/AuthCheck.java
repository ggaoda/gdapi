package com.gundam.gdapi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 * @author Gundam
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {


    /**
     * 必须有某个角色
     * 默认是 0 代表普通用户
     *
     */
    int mustRole() default 0;

}

