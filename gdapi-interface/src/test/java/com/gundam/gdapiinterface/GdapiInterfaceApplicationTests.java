package com.gundam.gdapiinterface;
import com.gundam.gdapiclientsdk.client.GdApiClient;
import com.gundam.gdapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class GdapiInterfaceApplicationTests {

    @Resource
    private GdApiClient gdApiClient;

    @Test
    void contextLoads() {


        String result1 = gdApiClient.getNameByGet("gaoda");
        String result2 = gdApiClient.getNameByPost("gaoda");
        String result3 = gdApiClient.getUserNameByPost(new User("gaoda"));
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);


    }

}
