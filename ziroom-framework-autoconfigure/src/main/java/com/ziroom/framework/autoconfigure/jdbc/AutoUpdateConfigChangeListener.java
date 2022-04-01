
package com.ziroom.framework.autoconfigure.jdbc;


import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * Create by zhangzheng on 2018/3/6
 * edit by ldm
 */
public class AutoUpdateConfigChangeListener implements ConfigChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(AutoUpdateConfigChangeListener.class);

    private final boolean typeConverterHasConvertIfNecessaryWithFieldParameter;
    private final Environment environment;
    private final ConfigurableBeanFactory beanFactory;
    private final TypeConverter typeConverter;
    private final PlaceholderHelper placeholderHelper;
    private final SpringValueRegistry springValueRegistry;
    private final AutoBeanFieldRegistry autoBeanFieldRegistry;
    private final Gson gson;

    public AutoUpdateConfigChangeListener(Environment environment, ConfigurableListableBeanFactory beanFactory) {
        this.typeConverterHasConvertIfNecessaryWithFieldParameter = testTypeConverterHasConvertIfNecessaryWithFieldParameter();
        this.beanFactory = beanFactory;
        this.typeConverter = this.beanFactory.getTypeConverter();
        this.environment = environment;
        this.placeholderHelper = SpringInjector.getInstance(PlaceholderHelper.class);
        this.springValueRegistry = SpringInjector.getInstance(SpringValueRegistry.class);
        this.autoBeanFieldRegistry = SpringInjector.getInstance(AutoBeanFieldRegistry.class);
        this.gson = new Gson();
    }

    @Override
    public void onChange(ConfigChangeEvent changeEvent) {
        Set<String> keys = changeEvent.changedKeys();
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (String key : keys) {
            // raw code
            // 1. check whether the changed key is relevant
            Collection<SpringValue> targetValues = springValueRegistry.get(beanFactory, key);
            if (targetValues == null || targetValues.isEmpty()) {
                continue;
            }
            // 2. check whether the value is really changed or not (since spring property sources have hierarchies)
            if (!shouldTriggerAutoUpdate(changeEvent, key)) {
                continue;
            }
            // 3. update the value
            for (SpringValue val : targetValues) {
                updateSpringValue(val);
            }
        }
        // judge auto scan bean field
        String namespace = changeEvent.getNamespace();
        for (String key : keys) {
            Collection<AutoBeanField> autoBeanFields = autoBeanFieldRegistry.get(beanFactory, namespace.toLowerCase() + "." + key);
            if (autoBeanFields == null || autoBeanFields.isEmpty()) {
                continue;
            }

            for (AutoBeanField beanField : autoBeanFields) {
                updateBeanField(beanField, changeEvent.getChange(key).getNewValue());
            }
        }
    }

    /**
     * Check whether we should trigger the auto update or not.
     * <br />
     * For added or modified keys, we should trigger auto update if the current value in Spring equals to the new value.
     * <br />
     * For deleted keys, we will trigger auto update anyway.
     */
    private boolean shouldTriggerAutoUpdate(ConfigChangeEvent changeEvent, String changedKey) {
        ConfigChange configChange = changeEvent.getChange(changedKey);

        if (configChange.getChangeType() == PropertyChangeType.DELETED) {
            return true;
        }

        return Objects.equals(environment.getProperty(changedKey), configChange.getNewValue());
    }

    private void updateSpringValue(SpringValue springValue) {
        try {
            Object value = resolvePropertyValue(springValue);
            springValue.update(value);

            logger.info("Auto update apollo changed value successfully, new value: {}, {}", value,
                    springValue);
        } catch (Throwable ex) {
            logger.error("Auto update apollo changed value failed, {}", springValue.toString(), ex);
        }
    }

    private void updateBeanField(AutoBeanField beanField, String newValue) {
        try {
            Object value = newValue;
//            Object value = resolveFieldValue(beanField);
            beanField.update(value);

            logger.info("Auto update apollo changed value successfully, new value: {}, {}", value,
                    beanField);
        } catch (Throwable ex) {
            logger.error("Auto update apollo changed value failed, {}", beanField.toString(), ex);
        }
    }

    /**
     * Logic transplanted from DefaultListableBeanFactory
     *
     * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#doResolveDependency(org.springframework.beans.factory.config.DependencyDescriptor, String, Set, TypeConverter)
     */
    private Object resolvePropertyValue(SpringValue springValue) {
        // value will never be null, as @Value and @ApolloJsonValue will not allow that
        Object value = placeholderHelper
                .resolvePropertyValue(beanFactory, springValue.getBeanName(), springValue.getPlaceholder());

        if (springValue.isJson()) {
            value = parseJsonValue((String) value, springValue.getGenericType());
        } else {
            if (springValue.isField()) {
                // org.springframework.beans.TypeConverter#convertIfNecessary(java.lang.Object, java.lang.Class, java.lang.reflect.Field) is available from Spring 3.2.0+
                if (typeConverterHasConvertIfNecessaryWithFieldParameter) {
                    value = this.typeConverter
                            .convertIfNecessary(value, springValue.getTargetType(), springValue.getField());
                } else {
                    value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType());
                }
            } else {
                value = this.typeConverter.convertIfNecessary(value, springValue.getTargetType(),
                        springValue.getMethodParameter());
            }
        }

        return value;
    }

    private Object resolveFieldValue(AutoBeanField beanField) {

        String prefix = beanField.getPrefix();

        ZiRoomApollo annoZ = AnnotationUtils.getAnnotation(beanField.getField(), ZiRoomApollo.class);
        if (annoZ != null) {
            prefix = prefix.concat(".").concat(annoZ.prefix());
        }
        Object value = placeholderHelper
                .resolvePropertyValue(beanFactory, beanField.getBeanName(), "${" + prefix + "." + beanField.getField().getName() + "}");
        if (typeConverterHasConvertIfNecessaryWithFieldParameter) {
            value = this.typeConverter.convertIfNecessary(value, beanField.getTargetType(), beanField.getField());
        } else {
            value = this.typeConverter.convertIfNecessary(value, beanField.getTargetType());
        }
        return value;
    }

    private Object parseJsonValue(String json, Type targetType) {
        try {
            return gson.fromJson(json, targetType);
        } catch (Throwable ex) {
            logger.error("Parsing json '{}' to type {} failed!", json, targetType, ex);
            throw ex;
        }
    }

    private boolean testTypeConverterHasConvertIfNecessaryWithFieldParameter() {
        try {
            TypeConverter.class.getMethod("convertIfNecessary", Object.class, Class.class, Field.class);
        } catch (Throwable ex) {
            return false;
        }

        return true;
    }
}
