package com.ziroom.framework.example;

import com.ziroom.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by liangrk on 2022/3/18.
 */
@SpringBootApplication
@EnableApolloConfig
public class Launcher {

    public static void main(String[] args) {
        SpringApplication.run(Launcher.class, args);
    }

}
