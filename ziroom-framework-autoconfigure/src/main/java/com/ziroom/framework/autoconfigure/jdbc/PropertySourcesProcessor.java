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
import com.ziroom.framework.autoconfigure.common.CommonMixUtils;
import com.ziroom.framework.autoconfigure.jdbc.definition.ZiroomDataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionValidationException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertyNameAliases;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.*;

import static com.ziroom.framework.autoconfigure.jdbc.PropertyConstants.DATA_TYPE;
import static com.ziroom.framework.autoconfigure.jdbc.definition.ZiroomDataSourceProvider.DATASOURCE_PREFIX;

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


    private static final Logger log = LoggerFactory.getLogger(PropertySourcesProcessor.class);

    private final static ConfigurationPropertyNameAliases aliases = new ConfigurationPropertyNameAliases();

    private ConfigurableEnvironment environment;
    private ZiroomDataSourceProvider ziroomDataSourceProvider;
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private static final String SPRING_JDBC_PREFIX = "spring.datasource.";
    private ApplicationContext applicationContext;

    static {
//        aliases.addAliases("driver-class-name", new String[]{"driverClassName"});
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        this.ziroomDataSourceProvider = new ZiroomDataSourceProvider();
        this.beanDefinitionRegistry = registry;
        ziroomDataSourceProvider.initialize();
        initializePropertySources();
    }

    private void initializePropertySources() {
//        boolean applicationDataSourceFlag = beanFactory.containsBeanDefinition(ExplicitUrl.class.getName());

        Set<Map.Entry<String, PropertySource<?>>> entries = ziroomDataSourceProvider.getZiroomDataSourceMap().entrySet();
        for (Map.Entry<String, PropertySource<?>> entry : entries) {

            if (ziroomDataSourceProvider.getZiroomDataSourceMap().size() == 1) {
                Properties properties = new Properties();
                Map<String, OriginTrackedValue> map = (Map<String, OriginTrackedValue>)entry.getValue().getSource();
                map.forEach((key, value) -> properties.put(key.replace(DATASOURCE_PREFIX,SPRING_JDBC_PREFIX), value.getValue()));
                environment.getPropertySources().addFirst(new PropertiesPropertySource(entry.getKey(),properties));
            } else {

                Binder binder = new Binder(ConfigurationPropertySources.from(entry.getValue()),
                        new PropertySourcesPlaceholdersResolver(environment));
                String type =  binder.bind(DATASOURCE_PREFIX + DATA_TYPE, Bindable.of(String.class)).get();
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
                dataSourceBuilder.addPropertyValue("propertySources", entry.getValue());
                dataSourceBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
                dataSourceBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);

                beanDefinitionRegistry.registerBeanDefinition(entry.getKey(), dataSourceBuilder.getBeanDefinition());
            }
        }
    }

    public static class ZiroomDataSourceFactoryBean implements FactoryBean<DataSource>, InitializingBean {

        DataSourceProperties dataSourceProperties;

        PropertySource<?> propertySources;

        public void setPropertySources(PropertySource<?> propertySources) {
            this.propertySources = propertySources;
        }

        ZiroomDataSourceFactoryBean() {}

        @Override
        public DataSource getObject() throws Exception {
            DataSource dataSource =  dataSourceProperties.initializeDataSourceBuilder().build();
            Bindable<DataSource> target = Bindable.ofInstance(dataSource);
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources),
                    new PropertySourcesPlaceholdersResolver(Arrays.asList(propertySources)));
            String type = binder.bind(DATASOURCE_PREFIX + DATA_TYPE, Bindable.of(String.class)).get();
            binder.bind("ziroom.datasource."+ getDataSourcePrefix(type), target);
            return  dataSource;
        }

        @Override
        public Class<?> getObjectType() {
            return DataSource.class;
        }

        @Override
        public void afterPropertiesSet() {
            dataSourceProperties = new DataSourceProperties();
            Bindable<DataSourceProperties> target = Bindable.ofInstance(dataSourceProperties);
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources),
                    new PropertySourcesPlaceholdersResolver(Arrays.asList(propertySources)));
            binder.bind("ziroom.datasource", target);
            //设置默认值
            if (Objects.isNull(dataSourceProperties.getType())){
                dataSourceProperties.setType(com.zaxxer.hikari.HikariDataSource.class);
            }
            if (CommonMixUtils.isBlank(dataSourceProperties.getDriverClassName())){
                dataSourceProperties.setDriverClassName("com.mysql.cj.jdbc.Driver");
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        //it is safe enough to cast as all known environment is derived from ConfigurableEnvironment
        this.environment = (ConfigurableEnvironment) environment;
    }

    /**
     * 通过字符串获取数据源class对象
     *
     * @param typeStr
     * @return
     */
    private Class<? extends DataSource> getDataSourceType(String typeStr) {
        Class<? extends DataSource> type;
        try {
            if (CommonMixUtils.isNotBlank(typeStr)) { //字符串不为空则通过反射获取class对象
                type = (Class<? extends DataSource>) Class.forName(typeStr);
            } else {
                type = HikariDataSource.class;  //默认为hikariCP数据源，与springboot默认数据源保持一致
            }
            return type;
        } catch (Exception e) {
            throw new IllegalArgumentException("can not resolve class with type: " + typeStr);
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


    private static String getDataSourcePrefix(String type) {
        switch (type) {
            case "org.apache.tomcat.jdbc.pool.DataSource":
                return "tomcat";
            case "org.apache.commons.dbcp2.BasicDataSource":
                return "dbcp2";
            default:
                return "hikari";
        }
    }
}