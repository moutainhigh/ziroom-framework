package com.ziroom.framework.ferrari.repository.core;

import com.google.common.collect.Lists;
import com.ziroom.framework.ferrari.repository.core.annotation.Column;
import com.ziroom.framework.ferrari.repository.core.constant.ApplicationConstant;
import com.ziroom.framework.ferrari.repository.core.constant.DBConstant;
import com.ziroom.framework.ferrari.repository.core.entity.BaseEntity;
import com.ziroom.framework.ferrari.repository.core.exception.DaoException;
import com.ziroom.framework.ferrari.repository.core.query.Update;
import com.ziroom.framework.ferrari.repository.core.util.FerrariStringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:33
 * @Version 1.0
 */
public class DaoHelper {
    private DaoHelper() {
    }

    public static Update entity2Update(Object entity, List<String> propetyList) {
        Update update = new Update();

        List<Field> fieldList = Lists.newArrayList();
        Class beanClass = entity.getClass();
        fieldList.addAll(Arrays.asList(entity.getClass().getDeclaredFields()));
        if (BaseEntity.class.isAssignableFrom(beanClass)) {
            fieldList.addAll(Arrays.asList(BaseEntity.class.getDeclaredFields()));
        }

        for (Field field : fieldList) {
            if (isFinalOrStatic(field)) {
                continue;
            }
            String propertyName = field.getName();
            //propetyList为空所有属性都需要更新，否则只更新包含的属性
            if (CollectionUtils.isEmpty(propetyList) || propetyList.contains(propertyName)) {
                update.set(propertyName, getColumnValue(field, entity));
            }
        }

        return update;
    }

    /**
     * Entity to Update With unrestricted super class.
     *
     * @param entity
     * @param propertyList
     * @return
     * @author mxj
     * @date 2018/6/19 11:51
     */
    public static Update entity2UpdateV2(Object entity, List<String> propertyList) {
        Update update = new Update();

        List<Field> fieldList = Lists.newArrayList();
        Class entityClass = entity.getClass();

        // Get local fields of the entity.
        Field[] fields = entityClass.getDeclaredFields();
        if (fields == null || fields.length == ApplicationConstant.INT_0) {
            throw new DaoException(entityClass.getCanonicalName() + " have no property");
        }
        fieldList.addAll(Arrays.asList(fields));

        // Get all fields of the Super Classes of the entity.
        // All the Super Classes.
        List<Class<?>> superClassList = getTraversalSuperClass(entityClass);
        for (Class<?> cls : superClassList) {
            fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
        }

        for (Field field : fieldList) {
            String propertyName = field.getName();
            // Ignore fields by final, static, and primary key.
            if (isFinalOrStatic(field) || propertyName.equals(DBConstant.PK_NAME)) {
                continue;
            }
            // Ignore fields by isTransient=false
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null && !columnAnnotation.isTransient()) {
                continue;
            }

            if (CollectionUtils.isEmpty(propertyList) || propertyList.contains(propertyName)) {
                update.set(propertyName, getColumnValue(field, entity));
            }
        }
        return update;
    }

    /**
     * 根据field得到对应值
     *
     * @param field - 字段对象
     * @param bean  - 对应的bean
     * @return - 返回filed值
     */
    public static Object getColumnValue(Field field, Object bean) {
        field.setAccessible(true);
        try {
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new DaoException("无法获取entity[" + bean + "]的属性[" + field.getName() + "]的值", e);
        }
    }

    /**
     * 根据field得到对应列名
     *
     * @param field - 字段对象
     * @return - 返回filed标注的列名注解
     */
    public static String getColumnName(Field field) {
        String propertyName = field.getName();
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation == null || FerrariStringUtils.isBlank(columnAnnotation.value())) {//column注解没有值,采用驼峰法取字段名
            return FerrariStringUtils.underscoreName(propertyName);
        } else {//使用column注解定义的字段名
            return columnAnnotation.value().toLowerCase();
        }
    }

    /**
     * 判断某个field是否常量或静态变量
     *
     * @param field
     * @return
     */
    public static boolean isFinalOrStatic(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers);
    }

    /**
     * Get the {@code Super Class}es of the {@code clazz}
     * by traversing through its super until "java.lang.Object".
     *
     * @param clazz
     * @return a list containing all of the super classes of
     * the {@code clazz} in proper sequence.
     * @author mxj
     * @date 2018/6/12 12:00
     */
    public static List<Class<?>> getTraversalSuperClass(Class<?> clazz) {
        List<Class<?>> superClassList = new ArrayList<>();
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            if (superclass.getName().equals("java.lang.Object")) {
                break;
            }
            superClassList.add(superclass);
            superclass = superclass.getSuperclass();
        }
        return superClassList;
    }
}
