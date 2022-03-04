package com.ziroom.framework.swagger.config;

import com.deepoove.swagger.dubbo.annotations.EnableDubboSwagger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDubboSwagger
public class SwaggerDubboConfig {
    @Bean
    public void log() {
        System.out.println("do SwaggerDubboConfig");
    }
}
