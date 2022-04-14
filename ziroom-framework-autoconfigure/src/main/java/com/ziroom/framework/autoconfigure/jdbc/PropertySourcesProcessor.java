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

import com.ziroom.framework.autoconfigure.jdbc.definition.ZiroomDataSourceProvider;
import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiroomDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.springframework.core.type.AnnotationMetadata;

/**
 * Apollo Property Sources processor for Spring Annotation Based Application. <br /> <br />
 * <p>
 * The reason why PropertySourcesProcessor implements {@link BeanFactoryPostProcessor} instead of
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor} is that lower versions of
 * Spring (e.g. 3.1.1) doesn't support registering BeanDefinitionRegistryPostProcessor in ImportBeanDefinitionRegistrar
 * - {@link com.ziroom.framework.autoconfigure.jdbc.OmegaConfigRegistrar}
 *
 * @author Jason Song(song_s@ctrip.com)
 */
public class PropertySourcesProcessor implements EnvironmentAware, ApplicationContextAware, ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        this.ziroomDataSourceProvider = new ZiroomDataSourceProvider();
        this.beanDefinitionRegistry = registry;
        ziroomDataSourceProvider.initialize();
        initializePropertySources();
    }

    private static final Logger log = LoggerFactory.getLogger(PropertySourcesProcessor.class);

    private ConfigurableEnvironment environment;
    private ZiroomDataSourceProvider ziroomDataSourceProvider;
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private static final String SPRING_JDBC_PREFIX = "spring.datasource.";
    private ApplicationContext applicationContext;

    private void initializePropertySources() {
//        boolean applicationDataSourceFlag = beanFactory.containsBeanDefinition(ExplicitUrl.class.getName());

        Set<Map.Entry<String, ZiroomDataSource>> entries = ziroomDataSourceProvider.getZiroomDataSourceMap().entrySet();
        for (Map.Entry<String, ZiroomDataSource> entry : entries) {
            final String prefix = SPRING_JDBC_PREFIX;
            if (ziroomDataSourceProvider.getZiroomDataSourceMap().size() == 1) {
                Properties properties = new Properties();
                entry.getValue().getProperties().forEach((key, value) -> properties.put(prefix + key, value));
                environment.getPropertySources().addFirst(new PropertiesPropertySource(entry.getKey(), properties));
            } else {
                Map<String, String> properties = entry.getValue().getProperties();
                String type = properties.get(PropertyConstants.DATA_TYPE);
                try {
                    Class.forName(type);
                } catch (ClassNotFoundException e) {
                    type = "com.zaxxer.hikari.HikariDataSource";
                    try {
                        Class.forName(type);
                    } catch (ClassNotFoundException ex) {
                        throw new BeanDefinitionValidationException("DataSource type not found");
                    }
                }
                BeanDefinitionBuilder dataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(ZiroomDataSourceFactoryBean.class);
//                properties.entrySet().forEach(propertie ->{
//                    dataSourceBuilder.addPropertyValue(propertie.getKey(),propertie.getValue());
//                });
//                dataSourceBuilder.setPrimary(Boolean.valueOf(entry.getValue().getConfig().getPrimary()));
                // clone properties
                dataSourceBuilder.addPropertyValue("properties", properties);
//                dataSourceBuilder.addPropertyValue(PropertyConstants.DATA_TYPE,type);
                dataSourceBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
                dataSourceBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);

                //AnnotatedGenericBeanDefinition
                beanDefinitionRegistry.registerBeanDefinition(entry.getKey(), dataSourceBuilder.getBeanDefinition());
            }
        }
    }

    public static class ZiroomDataSourceFactoryBean implements FactoryBean<DataSource>, InitializingBean {

        DataSourceProperties dataSourceProperties;

        Properties properties;

        public void setProperties(Properties properties) {
            this.properties = properties;
        }

        ZiroomDataSourceFactoryBean() {

        }


        @Override
        public DataSource getObject() throws Exception {
            return dataSourceProperties.initializeDataSourceBuilder().build();
        }

        @Override
        public Class<?> getObjectType() {
            return DataSource.class;
        }

        @Override
        public void afterPropertiesSet() {
            dataSourceProperties = new DataSourceProperties();
            Bindable<DataSourceProperties> target = Bindable.ofInstance(dataSourceProperties);
            Binder binder = new Binder(new MapConfigurationPropertySource(properties));
            binder.bind("", target);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        //it is safe enough to cast as all known environment is derived from ConfigurableEnvironment
        this.environment = (ConfigurableEnvironment) environment;
    }

    //    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.ziroomDataSourceProvider = new ZiroomDataSourceProvider();
        this.beanDefinitionRegistry = registry;
        ziroomDataSourceProvider.initialize();
        initializePropertySources();
    }

    //    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Class<? extends DataSource> genType(String type) {
        // todo Class.forName
        switch (type) {
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