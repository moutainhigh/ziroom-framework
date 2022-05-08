package com.ziroom.framework.autoconfigure.web;

import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liangrk on 2022/5/5.
 */
@ConditionalOnProperty(
        prefix = "ziroom.framework.weblog.",
        name = {"base-packages"}
)
@EnableConfigurationProperties(WebLogProperties.class)
@Configuration
@Import(AutoProxyRegistrar.class)
public class WebLogAutoConfiguration {

//    @Bean
//    public WebLogAspect webLogAspect(WebLogProperties webLogProperties) {
//        return new WebLogAspect(webLogProperties);
//    }

    @Bean
    public WebLogMethodInterceptor webLogMethodInterceptor() {
        return new WebLogMethodInterceptor();
    }

    @Bean
    public WeblogPointcut weblogPointcut(WebLogProperties webLogProperties) {
        return new WeblogPointcut(webLogProperties);
    }

    @Bean
    public DefaultBeanFactoryPointcutAdvisor webLogAdvisor(WebLogMethodInterceptor webLogMethodInterceptor, WeblogPointcut weblogPointcut) {
        DefaultBeanFactoryPointcutAdvisor webLogAdvisor = new DefaultBeanFactoryPointcutAdvisor();
        webLogAdvisor.setPointcut(weblogPointcut);
        webLogAdvisor.setAdvice(webLogMethodInterceptor);
        return webLogAdvisor;
    }


}
