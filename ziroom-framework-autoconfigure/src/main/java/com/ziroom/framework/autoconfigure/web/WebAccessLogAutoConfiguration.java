package com.ziroom.framework.autoconfigure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ziroom.framework.module.web.log.WebAccessLogFilter;
import com.ziroom.framework.module.web.log.WebAccessLogProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WebAccessLogProperties.class)
@ConditionalOnProperty(name = "ziroom.web.accesslog.enabled", havingValue = "true", matchIfMissing = true)
public class WebAccessLogAutoConfiguration {

    @Bean
    public FilterRegistrationBean<WebAccessLogFilter> logFilter(WebAccessLogProperties logProperties, ObjectMapper objectMapper) {
        final FilterRegistrationBean<WebAccessLogFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        final WebAccessLogFilter logFilter = new WebAccessLogFilter(logProperties, objectMapper);
        filterRegistrationBean.setFilter(logFilter);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        filterRegistrationBean.setName("webAccessLogFilter");
        return filterRegistrationBean;
    }


}
