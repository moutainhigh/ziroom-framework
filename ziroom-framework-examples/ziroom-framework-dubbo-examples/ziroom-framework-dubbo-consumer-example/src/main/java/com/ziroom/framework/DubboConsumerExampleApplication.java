package com.ziroom.framework;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by liangrk on 2022/3/12.
 */
@SpringBootApplication
@EnableDubbo
public class DubboConsumerExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(DubboConsumerExampleApplication.class, args);
    }

}
