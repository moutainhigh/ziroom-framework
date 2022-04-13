package com.ziroom.framework.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by liangrk on 2022/3/18.
 */
@SpringBootApplication
@MapperScan("com.ziroom.framework.example.dao")
public class Launcher {

    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }

}
