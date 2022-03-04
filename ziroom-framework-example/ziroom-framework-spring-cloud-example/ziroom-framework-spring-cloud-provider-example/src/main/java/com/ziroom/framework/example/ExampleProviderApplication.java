package com.ziroom.framework.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient(autoRegister = false)
@SpringBootApplication
public class ExampleProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleProviderApplication.class, args);
    }

}
