package com.ziroom.framework.autoconfigure.jdbc;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.BeanFactory;

import java.util.Collection;
import java.util.Map;

public class AutoBeanFieldRegistry {
    private final Map<BeanFactory, Multimap<String, AutoBeanField>> registry = Maps.newConcurrentMap();
    private final Object LOCK = new Object();
    public void register(BeanFactory beanFactory, String key, AutoBeanField beanField) {
        if (!registry.containsKey(beanFactory)) {
            synchronized (LOCK) {
                if (!registry.containsKey(beanFactory)) {
                    registry.put(beanFactory, LinkedListMultimap.create());
                }
            }
        }

        registry.get(beanFactory).put(key, beanField);
    }

    public Collection<AutoBeanField> get(BeanFactory beanFactory, String key) {
        Multimap<String, AutoBeanField> beanFactoryBeanFields = registry.get(beanFactory);
        if (beanFactoryBeanFields == null) {
            return null;
        }
        return beanFactoryBeanFields.get(key);
    }
}
