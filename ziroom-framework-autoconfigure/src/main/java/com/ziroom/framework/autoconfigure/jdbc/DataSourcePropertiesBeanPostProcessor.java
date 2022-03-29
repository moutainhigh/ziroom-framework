package com.ziroom.framework.autoconfigure.jdbc;

import com.ziroom.framework.autoconfigure.jdbc.definition.ZiRoomDataSourceProvider;
import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiRoomDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Objects;

public class DataSourcePropertiesBeanPostProcessor implements BeanPostProcessor, ApplicationContextInitializer {

    private ConfigurableApplicationContext context;

    private static final String prefix = "jdbc";

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        this.context = configurableApplicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties) {
            ZiRoomDataSourceProvider provider = context.getBean(ZiRoomDataSourceProvider.class);
            if (Objects.isNull(provider.getZiRoomDataSource())){
                return bean;
            }
            DataSourceProperties dataSourceProperties = (DataSourceProperties) bean;
            ZiRoomDataSource ziRoomDataSource = provider.getZiRoomDataSource();
            if (!prefix.equals(ziRoomDataSource.getPrefix())){
                return bean;
            }
            dataSourceProperties.setUsername(ziRoomDataSource.getProperties().getUsername());
            dataSourceProperties.setPassword(ziRoomDataSource.getProperties().getPassword());
            dataSourceProperties.setUrl(ziRoomDataSource.getProperties().getUrl());
            dataSourceProperties.setName(ziRoomDataSource.getProperties().getName());
            return dataSourceProperties;
        }
        return bean;
    }
}