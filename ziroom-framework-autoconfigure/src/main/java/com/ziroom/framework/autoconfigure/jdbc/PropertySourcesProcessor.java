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

import com.ziroom.framework.autoconfigure.jdbc.definition.ZiRoomDataSourceProvider;
import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiRoomDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
public class PropertySourcesProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ApplicationContextAware {

    private static final Log log = LogFactory.getLog(PropertySourcesProcessor.class);

    private ConfigurableEnvironment environment;
    private ZiRoomDataSourceProvider ziRoomDataSourceProvider;
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private static final String SPRING_JDBC_PREFIX = "spring.datasource.";
    private ApplicationContext applicationContext;

    private void initializePropertySources() {
//        boolean applicationDataSourceFlag = beanFactory.containsBeanDefinition(ExplicitUrl.class.getName());

        Set<Map.Entry<String, ZiRoomDataSource>> entries =  ziRoomDataSourceProvider.getZiRoomDataSourceMap().entrySet();
        for (Map.Entry<String, ZiRoomDataSource> entry : entries){
            final String prefix = SPRING_JDBC_PREFIX;
            if (ziRoomDataSourceProvider.getZiRoomDataSourceMap().size() ==  1){
                Properties properties = new Properties();
                entry.getValue().getProperties().entrySet().forEach(
                        propertiesEntry ->{
                            properties.put(prefix + propertiesEntry.getKey(),propertiesEntry.getValue());
                        }
                );
                environment.getPropertySources().addFirst(new PropertiesPropertySource(entry.getKey(),properties));
            }else{
                Map<String,String> properties = entry.getValue().getProperties();
                String type = properties.get(PropertyConstants.DATA_TYPE);
                try {
                    Class.forName(type);
                }catch (ClassNotFoundException e) {
                    type = "com.zaxxer.hikari.HikariDataSource";
                }
                BeanDefinitionBuilder dataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(genType(type));
                properties.entrySet().forEach(propertie ->{
                    dataSourceBuilder.addPropertyValue(propertie.getKey(),propertie.getValue());
                });
                dataSourceBuilder.setPrimary(Boolean.valueOf(entry.getValue().getConfig().getPrimary()));
                dataSourceBuilder.addPropertyValue(PropertyConstants.DATA_TYPE,type);
                dataSourceBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
                dataSourceBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
                //AnnotatedGenericBeanDefinition
                beanDefinitionRegistry.registerBeanDefinition(entry.getKey(), dataSourceBuilder.getBeanDefinition());
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        //it is safe enough to cast as all known environment is derived from ConfigurableEnvironment
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.ziRoomDataSourceProvider = new ZiRoomDataSourceProvider();
        this.beanDefinitionRegistry = registry;
        ziRoomDataSourceProvider.initialize();
        initializePropertySources();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Class<? extends DataSource> genType (String type){
        // todo Class.forName
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