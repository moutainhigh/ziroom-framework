/*
 * Copyright ziroom.com.
 */
package com.ziroom.framework.autoconfigure.cache;

import com.zaxxer.hikari.HikariDataSource;
import com.ziroom.framework.autoconfigure.common.CommonMixUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
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
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.ziroom.framework.autoconfigure.amqp.ZiroomRabbitProvider.RABBIT_PREFIX;

/**
 * 自如rabbitmq自动注册处理
 * @author zhaoy13,liangrk,kanggh
 */
public class RabbitPropertySourcesProcessor implements EnvironmentAware, ApplicationContextAware, ImportBeanDefinitionRegistrar {


    private static final Logger log = LoggerFactory.getLogger(RabbitPropertySourcesProcessor.class);

    private ConfigurableEnvironment environment;
    private ZiroomRabbitProvider ziroomRabbitProvider;
    private BeanDefinitionRegistry beanDefinitionRegistry;
    private static final String SPRING_RABBITMQ_PREFIX = "spring.rabbitmq.";
    private ApplicationContext applicationContext;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        this.ziroomRabbitProvider = new ZiroomRabbitProvider();
        this.beanDefinitionRegistry = registry;
        ziroomRabbitProvider.initialize();
        initializePropertySources();
    }

    private void initializePropertySources() {

        Set<Map.Entry<String, PropertySource<?>>> entries = ziroomRabbitProvider.getZiroomRabbitMap().entrySet();
        for (Map.Entry<String, PropertySource<?>> entry : entries) {

            if (ziroomRabbitProvider.getZiroomRabbitMap().size() == 1) {
                Properties properties = new Properties();
                Map<String, OriginTrackedValue> map = (Map<String, OriginTrackedValue>)entry.getValue().getSource();
                map.forEach((key, value) -> properties.put(key.replace(RABBIT_PREFIX,SPRING_RABBITMQ_PREFIX), value.getValue()));
                environment.getPropertySources().addFirst(new PropertiesPropertySource(entry.getKey(),properties));
            } else {
//
//                Binder binder = new Binder(ConfigurationPropertySources.from(entry.getValue()),
//                        new PropertySourcesPlaceholdersResolver(environment));
//                String type =  binder.bind(DATASOURCE_PREFIX + DATA_TYPE, Bindable.of(String.class)).get();
//                try {
//                    Class.forName(type);
//                } catch (ClassNotFoundException e) {
//                    type = "com.zaxxer.hikari.HikariDataSource";
//                    try {
//                        Class.forName(type);
//                    } catch (ClassNotFoundException ex) {
//                        throw new BeanDefinitionValidationException("DataSource type not found");
//                    }
//                }
                BeanDefinitionBuilder dataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(ZiroomRabbitFactoryBean.class);
                dataSourceBuilder.addPropertyValue("propertySources", entry.getValue());
                dataSourceBuilder.setScope(BeanDefinition.SCOPE_SINGLETON);
                dataSourceBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);

                beanDefinitionRegistry.registerBeanDefinition(entry.getKey(), dataSourceBuilder.getBeanDefinition());
            }
        }
    }

    public static class ZiroomRabbitFactoryBean implements FactoryBean<DataSource>, InitializingBean {

        DataSourceProperties dataSourceProperties;

        PropertySource<?> propertySources;

        public void setPropertySources(PropertySource<?> propertySources) {
            this.propertySources = propertySources;
        }

        ZiroomRabbitFactoryBean() {}

        @Override
        public DataSource getObject() throws Exception {
            DataSource dataSource =  dataSourceProperties.initializeDataSourceBuilder().build();
            Bindable<DataSource> target = Bindable.ofInstance(dataSource);
            Binder binder = new Binder(ConfigurationPropertySources.from(propertySources),
                    new PropertySourcesPlaceholdersResolver(Arrays.asList(propertySources)));
            String type =  "";//binder.bind(DATASOURCE_PREFIX + DATA_TYPE, Bindable.of(String.class)).get();
            binder.bind("ziroom.rabbitmq."+ getDataSourcePrefix(type), target);
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
            binder.bind("ziroom.rabbitmq", target);
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