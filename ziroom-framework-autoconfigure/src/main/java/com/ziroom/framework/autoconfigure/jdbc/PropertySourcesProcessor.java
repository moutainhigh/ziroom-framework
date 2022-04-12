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
import com.ziroom.framework.autoconfigure.utils.SpringInjector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertiesPropertySource;

import javax.sql.DataSource;
import java.util.Map;
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
public class PropertySourcesProcessor implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, PriorityOrdered, ApplicationContextAware {

    private static final Log log = LogFactory.getLog(ZiRoomDataSourceProvider.class);

    private ConfigurableEnvironment environment;
    private ZiRoomDataSourceProvider ziRoomDataSourceProvider;
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private static final String SPRING_JDBC_PREFIX = "spring.datasource.";
    private ApplicationContext applicationContext;

    private void initializePropertySources() {
//        boolean applicationDataSourceFlag = beanFactory.containsBeanDefinition(ExplicitUrl.class.getName());
        ziRoomDataSourceProvider.getZiRoomDataSourceMap().entrySet().stream().forEach(entry ->{

            final String prefix = SPRING_JDBC_PREFIX;
            if (ziRoomDataSourceProvider.getZiRoomDataSourceMap().size() ==  1){
                Properties properties = new Properties();
                entry.getValue().getProperties().entrySet().forEach(
                        propertiesEntry ->{
                            properties.put(prefix + propertiesEntry.getKey(),propertiesEntry.getValue());
                            log.warn(String.format("数据库链接信息已被替换成omega平台配置,%s,值为: %s",
                                    propertiesEntry.getKey(), propertiesEntry.getValue()));
                        }
                );
                environment.getPropertySources().addFirst(new PropertiesPropertySource(entry.getKey(),properties));
            }else{
//                String tablePrefix = SPRING_JDBC_PREFIX + entry.getKey()+".";
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
                dataSourceBuilder.addPropertyValue(PropertyConstants.DATA_TYPE,type);

                ConfigurationPropertiesBinder binder = new ConfigurationPropertiesBinder(applicationContext);
                BindHandler bindHandler = binder.getHandler();
                switch (entry.getKey()) {
                    case "org.apache.tomcat.jdbc.pool.DataSource":
                        ResolvableType bindType = ResolvableType.forClass(org.apache.tomcat.jdbc.pool.DataSource.class);
                        Bindable<Object> bindTarget = Bindable.of(bindType).withAnnotations(null);
                        if (entry.getValue() != null) {
                            bindTarget = bindTarget.withExistingValue(entry.getValue());
                        }
                        binder.getBinder().bind("spring.datasource.tomcat", bindTarget, bindHandler);
                        break;
                    case "org.apache.commons.dbcp2.BasicDataSource":
                        bindType = ResolvableType.forClass(org.apache.commons.dbcp2.BasicDataSource.class);
                        bindTarget = Bindable.of(bindType).withAnnotations(null);
                        if (entry.getValue() != null) {
                            bindTarget = bindTarget.withExistingValue(entry.getValue());
                        }
                        binder.getBinder().bind("spring.datasource.dbcp2", bindTarget, bindHandler);
                        break;
                    default:
                        bindType = ResolvableType.forClass(com.zaxxer.hikari.HikariDataSource.class);
                        bindTarget = Bindable.of(bindType).withAnnotations(null);
                        if (entry.getValue() != null) {
                            bindTarget = bindTarget.withExistingValue(entry.getValue());
                        }
                        binder.getBinder().bind("spring.datasource.hikari", bindTarget, bindHandler);
                }
//                hikariDataSource.setPoolName(entry.getKey());
                beanDefinitionRegistry.registerBeanDefinition(entry.getKey(), dataSourceBuilder.getBeanDefinition());
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
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.ziRoomDataSourceProvider = SpringInjector.getInstance(ZiRoomDataSourceProvider.class);
        this.beanDefinitionRegistry = registry;
        ziRoomDataSourceProvider.initialize();
        initializePropertySources();
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // todo 去掉Guice， 尽可能减少对外部框架的依赖，容易造成业务项目冲突
//        this.ziRoomDataSourceProvider = SpringInjector.getInstance(ZiRoomDataSourceProvider.class);
//        this. = beanFactory;
//        ziRoomDataSourceProvider.initialize();
//        initializePropertySources();
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