package com.gundam.gdapiclientsdk;

import com.gundam.gdapiclientsdk.client.GdApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("gdapi.client")
@ComponentScan
public class GdApiClientConfig {

    private String accessKey;
    private String secretKey;

    /**
     * 自动装配,通过ak ,sk生成客户端
     * @return
     */
    @Bean
    public GdApiClient gdApiClient(){
        return new GdApiClient(accessKey,secretKey);
    }

}
