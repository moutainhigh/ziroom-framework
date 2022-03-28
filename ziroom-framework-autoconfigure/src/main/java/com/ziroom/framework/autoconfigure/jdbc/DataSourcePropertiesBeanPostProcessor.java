package com.ziroom.framework.autoconfigure.jdbc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class DataSourcePropertiesBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties) {
            DataSourceProperties dataSourceProperties = (DataSourceProperties) bean;
            dataSourceProperties.setUsername("Ha");
            dataSourceProperties.setPassword("zaaa");
            dataSourceProperties.setUrl("jdbc://localhost");
            return dataSourceProperties;
        }
        return bean;
    }
}