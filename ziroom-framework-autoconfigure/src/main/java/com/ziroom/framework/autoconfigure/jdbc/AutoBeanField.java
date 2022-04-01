package com.ziroom.framework.autoconfigure.jdbc;

import org.springframework.core.MethodParameter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * auto scan bean field value
 */
public class AutoBeanField {

    private MethodParameter methodParameter;
    private Field field;
    private Object bean;
    private String beanName;
    private String key;
    private String prefix;
    private Class<?> targetType;
    private Type genericType;
    private boolean isJson;

    public AutoBeanField(String key, String prefix, Object bean, String beanName, Field field, boolean isJson) {
        this.bean = bean;
        this.beanName = beanName;
        this.field = field;
        this.key = key;
        this.prefix = prefix;
        this.targetType = field.getType();
        this.isJson = isJson;
        if(isJson){
            this.genericType = field.getGenericType();
        }
    }

    public AutoBeanField(String key, String prefix, Object bean, String beanName, Method method, boolean isJson) {
        this.bean = bean;
        this.beanName = beanName;
        this.methodParameter = new MethodParameter(method, 0);
        this.key = key;
        this.prefix = prefix;
        Class<?>[] paramTps = method.getParameterTypes();
        this.targetType = paramTps[0];
        this.isJson = isJson;
        if(isJson){
            this.genericType = method.getGenericParameterTypes()[0];
        }
    }

    public void update(Object newVal) throws IllegalAccessException, InvocationTargetException {
        if (isField()) {
            injectField(newVal);
        } else {
            injectMethod(newVal);
        }
    }

    private void injectField(Object newVal) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        field.set(bean, newVal);
        field.setAccessible(accessible);
    }

    private void injectMethod(Object newVal)
            throws InvocationTargetException, IllegalAccessException {
        methodParameter.getMethod().invoke(bean, newVal);
    }

    public String getBeanName() {
        return beanName;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public MethodParameter getMethodParameter() {
        return methodParameter;
    }

    public boolean isField() {
        return this.field != null;
    }

    public Field getField() {
        return field;
    }

    public Type getGenericType() {
        return genericType;
    }

    public boolean isJson() {
        return isJson;
    }

    @Override
    public String toString() {
        if (isField()) {
            return String
                    .format("key: %s, beanName: %s, field: %s.%s", key, beanName, bean.getClass().getName(), field.getName());
        }
        return String.format("key: %s, beanName: %s, method: %s.%s", key, beanName, bean.getClass().getName(),
                methodParameter.getMethod().getName());
    }
}
