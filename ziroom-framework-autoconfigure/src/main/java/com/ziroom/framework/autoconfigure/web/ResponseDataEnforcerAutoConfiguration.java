package com.ziroom.framework.autoconfigure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ziroom.framework.module.web.response.EnforceResponseDataAdvice;
import com.ziroom.framework.module.web.response.WebResponseProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
@ConditionalOnProperty(name = "ziroom.web.response.enforce.enabled", havingValue = "true")
@ConditionalOnClass(name = "com.ziroom.framework.module.web.response.EnforceResponseDataAdvice")
@EnableConfigurationProperties(WebResponseProperties.class)
public class ResponseDataEnforcerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(EnforceResponseDataAdvice.class)
    public EnforceResponseDataAdvice responseEnvelopeAdvice(WebResponseProperties properties, ObjectMapper stringResponseMapper) {
        return new EnforceResponseDataAdvice(properties, stringResponseMapper);
    }

}
