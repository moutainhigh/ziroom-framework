/*
 * Copyright ziroom.com.
 */

package com.ziroom.framework.autoconfigure.jdbc;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * 自如数据库自动注册
 * @author zhaoy13,liangrk,kanggh
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@AutoConfigureBefore(DataSourceAutoConfiguration.class)
@Import({DataSourcePropertySourcesProcessor.class})
@ConditionalOnMissingBean(type = "io.r2dbc.spi.ConnectionFactory")
public class ZiroomDataSourceAutoConfiguration {


//    @Bean
//    @ConditionalOnMissingBean(type = "org.springframework.context.support.PropertySourcesPlaceholderConfigurer")
//    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
//        return new PropertySourcesPlaceholderConfigurer();
//    }

}
