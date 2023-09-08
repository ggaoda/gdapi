package com.gundam.gdapi.config;

import com.gundam.gdapi.utils.LoginInterceptor;
import com.gundam.gdapi.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 全局跨域配置
 * @author Gundam
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void addInterceptors(InterceptorRegistry registry){




        registry.addInterceptor(new LoginInterceptor()).excludePathPatterns(
                "/user/register",
                "/user/login",
                "/user/get/login",
                "/interfaceInfo/list/page",
                "/v2/api-docs",
                "/user/interface/**",
                "/user/order/**",
                "/user/loginBySms",
                "/user/getCaptcha",
                "/third/alipay/**",
                "/interfaceInfo/sdk",
                "/user/smsCaptcha",
                "/user/email/register",
                "/user/update/avatar"

        ).order(2);

        //刷新token
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(1);


    }
}
