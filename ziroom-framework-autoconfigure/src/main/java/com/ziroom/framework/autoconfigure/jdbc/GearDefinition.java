package com.ziroom.framework.autoconfigure.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

@Slf4j
public class GearDefinition implements BeanDefinitionRegistryPostProcessor {

    static BeanDefinitionRegistry registry;

    static ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public static boolean register(ConfigurableListableBeanFactory configurableListableBeanFactory, String beanName, Object bean, String initMethod, String destroyMethod) {
        if (configurableListableBeanFactory.containsBean(beanName)) {
            log.error("exists bean named {} when register bean");
            return false;
        }

        configurableListableBeanFactory.registerSingleton(beanName, bean);
//        GenericBeanDefinition beanDefinition = (GenericBeanDefinition) configurableListableBeanFactory.getBeanDefinition(beanName);
//        beanDefinition.setInitMethodName(initMethod);
//        beanDefinition.setDestroyMethodName(destroyMethod);

        return true;
    }

    public static boolean remove(BeanDefinitionRegistry registry, String beanName) {

        if (registry.containsBeanDefinition(beanName)) {
            registry.removeBeanDefinition(beanName);
        }
        if (((DefaultListableBeanFactory) registry).containsBean(beanName)) {
            ((DefaultListableBeanFactory) registry).destroySingleton(beanName);
        }

        return true;
    }

    public static boolean registerAndRemove(ConfigurableListableBeanFactory configurableListableBeanFactory, BeanDefinitionRegistry registry, String beanName, Object bean, String initMethod, String destroyMethod) {
        remove(registry, beanName);
        register(configurableListableBeanFactory, beanName, bean, initMethod, destroyMethod);
        return true;
    }

    public static boolean registerAndRemove(String beanName, Object bean, String initMethod, String destroyMethod) {
        remove(registry, beanName);
        register(beanFactory, beanName, bean, initMethod, destroyMethod);
        return true;
    }

}
