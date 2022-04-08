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
import com.ziroom.framework.autoconfigure.jdbc.definition.ZiRoomDataSourceProvider;
import com.ziroom.framework.autoconfigure.utils.SpringInjector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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
//    private static final Multimap<Integer, String> NAMESPACE_NAMES = LinkedHashMultimap.create();
//    private static final Set<BeanFactory> AUTO_UPDATE_INITIALIZED_BEAN_FACTORIES = Sets.newConcurrentHashSet();

    private ConfigurableEnvironment environment;
    private ZiRoomDataSourceProvider ziRoomDataSourceProvider;
    private ConfigurableListableBeanFactory beanFactory;
    private static final String SPRING_JDBC_PREFIX = "spring.datasource.";

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.ziRoomDataSourceProvider = SpringInjector.getInstance(ZiRoomDataSourceProvider.class);
        this.beanFactory = beanFactory;
        ziRoomDataSourceProvider.initialize();
        initializePropertySources();
    }

    private void initializePropertySources() {
//        if (environment.getPropertySources().contains(PropertySourcesConstants.APOLLO_PROPERTY_SOURCE_NAME)) {
//            //already initialized
//            return;
//        }
        //判断
//        PathMatchingResourcePatternResolver resourcePatternResolver = SpringInjector.getInstance(PathMatchingResourcePatternResolver.class);
//        Resource[] resources = resourcePatternResolver.getResources("classpath:application.*");
//        boolean sourceConfigFlag = false;
//        for(Resource resource : resources){
//
//        }
        ziRoomDataSourceProvider.getZiRoomDataSourceMap().entrySet().stream().forEach(entry ->{
            Properties properties = new Properties();
            //单默认数据源时不实用数据库名前缀
            String prefix = SPRING_JDBC_PREFIX;
//            if (ziRoomDataSourceProvider.getZiRoomDataSourceMap().size() ==  1){
                properties.setProperty(prefix+"driver-class-name",entry.getValue().getProperties().getDriver());
                properties.setProperty(prefix+"url",entry.getValue().getProperties().getUrl());
                properties.setProperty(prefix+"username",entry.getValue().getProperties().getUsername());
                properties.setProperty(prefix+"password",entry.getValue().getProperties().getPassword());
                properties.setProperty(prefix+"name",entry.getKey());
                environment.getPropertySources().addFirst(new PropertiesPropertySource(entry.getKey(),properties));
//            }else{
                prefix = SPRING_JDBC_PREFIX+entry.getKey()+".";
            HikariDataSource dataSource = DataSourceBuilder.create(this.getClass().getClassLoader())
//                        .type(entry.getValue().getProperties().getType())
                        .driverClassName(entry.getValue().getProperties().getDriver())
                        .url(entry.getValue().getProperties().getUrl())
                        .username(entry.getValue().getProperties().getUsername())
                        .password(entry.getValue().getProperties().getPassword())
                        .type(HikariDataSource.class).build();
                dataSource.setPoolName(entry.getKey());
                beanFactory.registerSingleton(entry.getKey(),dataSource);
//            }
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
}