package com.gundam.gdapi.utils;

import com.gundam.gdapi.common.ErrorCode;
import com.gundam.gdapi.exception.BusinessException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        

        // 判断是否拦截(线程中是否存在用户)
        if (UserHolder.getUser() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return true;
    }
}
