package com.ziroom.framework.autoconfigure.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcAutoConfiguration extends WebMvcConfigurationSupport {

    @Bean
    public DefaultExceptionHandler exceptionHandler() {
        return new DefaultExceptionHandler();
    }

}
