package com.gundam.gdapi.service;

import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 用户服务测试
 *
 * @author Gundam
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userAccount = "gaodaaa";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        String vipCode = "2";
        try {
            long result = userService.userRegister(userAccount, userPassword, checkPassword,vipCode);
            Assertions.assertEquals(-1, result);
            userAccount = "yu";
            result = userService.userRegister(userAccount, userPassword, checkPassword,vipCode);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }
}
