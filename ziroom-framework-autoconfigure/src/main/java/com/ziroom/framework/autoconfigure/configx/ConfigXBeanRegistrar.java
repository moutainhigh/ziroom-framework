package com.ziroom.framework.autoconfigure.configx;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

public abstract class ConfigXBeanRegistrar<T> implements EnvironmentAware, ImportBeanDefinitionRegistrar {

    private ConfigurableEnvironment environment;

    private final Class<T> configurationPropertiesClass;
    private final String prefix;

    public ConfigXBeanRegistrar(Class<T> configurationPropertiesClass, String prefix) {
        this.configurationPropertiesClass = configurationPropertiesClass;
        this.prefix = prefix;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        bind().ifBound(list -> {

            list.forEach(c -> register(c, registry, importBeanNameGenerator));

            afterBeanRegistration(list, registry, importBeanNameGenerator);
        });
    }

    private BindResult<? extends List<T>> bind() {
        Bindable<? extends List<T>> bindable = Bindable.listOf(this.configurationPropertiesClass);
        Binder binder = Binder.get(this.environment);
        return binder.bind(prefix, bindable);
    }

    abstract protected void register(T configurationProperties, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator);

    abstract protected void afterBeanRegistration(List<T> list, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator);


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }
}
