package com.gundam.gdapiinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GdapiInterfaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GdapiInterfaceApplication.class, args);
    }

}
