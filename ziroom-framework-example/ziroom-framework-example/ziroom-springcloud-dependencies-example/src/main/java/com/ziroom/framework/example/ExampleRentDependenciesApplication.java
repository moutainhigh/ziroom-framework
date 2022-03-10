package com.ziroom.framework.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient(autoRegister = false)
@EnableFeignClients
public class ExampleRentDependenciesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExampleRentDependenciesApplication.class, args);
    }

}
