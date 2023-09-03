package com.gaoda.apithirdparty;

import cn.hutool.json.JSONUtil;
import com.gundam.gdapicommon.model.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class ApiThirdPartyServicesApplicationTests {

    @Resource
    private RedisTemplate<Object,Object> redisTemplate;


    @Test
    void contextLoads() {
        String userId = (String) redisTemplate.opsForValue().get("api:login:user:gaoda85d337fd-7f44-4ab3-aa45-f6ac9732172c1");

        System.out.println(userId);
    }

}