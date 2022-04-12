/*
 * Copyright 2022 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ziroom.framework.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import com.ziroom.framework.autoconfigure.jdbc.config.ExplicitUrl;
import com.ziroom.framework.autoconfigure.jdbc.definition.ZiRoomDataSourceProvider;
import com.ziroom.framework.autoconfigure.utils.SpringInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * Apollo Property Sources processor for Spring Annotation Based Application. <br /> <br />
 *
 * The reason why PropertySourcesProcessor implements {@link BeanFactoryPostProcessor} instead of
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor} is that lower versions of
 * Spring (e.g. 3.1.1) doesn't support registering BeanDefinitionRegistryPostProcessor in ImportBeanDefinitionRegistrar
 * - {@link com.ziroom.framework.autoconfigure.jdbc.OmegaConfigRegistrar}
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class PropertySourcesProcessor implements BeanFactoryPostProcessor, EnvironmentAware, PriorityOrdered {

    private ConfigurableEnvironment environment;
    private ZiRoomDataSourceProvider ziRoomDataSourceProvider;
    private ConfigurableListableBeanFactory beanFactory;
    private static final String SPRING_JDBC_PREFIX = "spring.datasource.";

    private void initializePropertySources() {
//        boolean applicationDataSourceFlag = beanFactory.containsBeanDefinition(ExplicitUrl.class.getName());
        ziRoomDataSourceProvider.getZiRoomDataSourceMap().entrySet().stream().forEach(entry ->{
            final String prefix = SPRING_JDBC_PREFIX;

            if (ziRoomDataSourceProvider.getZiRoomDataSourceMap().size() ==  1){
                Properties properties = new Properties();
                //todo 这里需要打印日志
                entry.getValue().getProperties().entrySet().stream().forEach(
                        propertiesEntry ->{
                            properties.put(prefix + propertiesEntry.getKey(),propertiesEntry.getValue());
                        }
                );
                environment.getPropertySources().addFirst(new PropertiesPropertySource(entry.getKey(),properties));
            }else{
//                String tablePrefix = SPRING_JDBC_PREFIX + entry.getKey()+".";
                String type = entry.getValue().getProperties().get(PropertySourcesConstants.DATA_TYPE);
                try {
                    getClass().getClassLoader().loadClass(entry.getValue().getProperties().get(PropertySourcesConstants.DATA_TYPE));
                }catch (ClassNotFoundException e) {
                    type = "com.zaxxer.hikari.HikariDataSource";
                }
                DataSource dataSource = DataSourceBuilder.create(this.getClass().getClassLoader())
                        .driverClassName(entry.getValue().getProperties().get(PropertySourcesConstants.DATA_DRIVER_CLASS_NAME))
                        .url(entry.getValue().getProperties().get(PropertySourcesConstants.DATA_URL))
                        .username(entry.getValue().getProperties().get(PropertySourcesConstants.DATA_USERNAME))
                        .password(entry.getValue().getProperties().get(PropertySourcesConstants.DATA_PASSWORD))
                        .type(genType(type)).build();
                if (dataSource instanceof HikariDataSource){
                    HikariDataSource hikariDataSource = (HikariDataSource)dataSource;
                    hikariDataSource.setPoolName(entry.getKey());
                    beanFactory.registerSingleton(entry.getKey(),hikariDataSource);
                    if (Boolean.valueOf(entry.getValue().getConfig().getPrimary())){
                        beanFactory.registerSingleton("datasource",hikariDataSource);
                    }
                }else {
                    beanFactory.registerSingleton(entry.getKey(),dataSource);
                }
            }
        });
    }

    @Override
    public void setEnvironment(Environment environment) {
        //it is safe enough to cast as all known environment is derived from ConfigurableEnvironment
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public int getOrder() {
        //make it as early as possible
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.ziRoomDataSourceProvider = SpringInjector.getInstance(ZiRoomDataSourceProvider.class);
        this.beanFactory = beanFactory;
        ziRoomDataSourceProvider.initialize();
        initializePropertySources();
    }

    private Class<? extends DataSource> genType (String type){
        switch (type){
            case "org.apache.tomcat.jdbc.pool.DataSource":
                return org.apache.tomcat.jdbc.pool.DataSource.class;
            case "org.apache.commons.dbcp2.BasicDataSource":
                return org.apache.commons.dbcp2.BasicDataSource.class;
            case "com.zaxxer.hikari.HikariDataSource":
                return com.zaxxer.hikari.HikariDataSource.class;
        }
        return com.zaxxer.hikari.HikariDataSource.class;
    }
}