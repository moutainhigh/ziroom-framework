package com.ziroom.ferrari.repository.core.jdbc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ziroom.ferrari.repository.core.DaoHelper;
import com.ziroom.ferrari.repository.core.annotation.Column;
import com.ziroom.ferrari.repository.core.exception.DaoException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ziroom.ferrari.repository.core.constant.ApplicationConstant.INT_0;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:30
 * @Version 1.0
 */
public final class EntityMapper<T> {
    private String entityName;

    /**
     * 属性名到字段名映射
     */
    private Map<String, String> propertyToColumnMapper = Maps.newLinkedHashMap();

    /**
     * 字段名到属性名映射
     */
    private Map<String, String> columnToPropertyMapper = Maps.newLinkedHashMap();

    /**
     * 不需要持久化的字段
     */
    private Set<String> notNeedTransientPropertySet = Sets.newHashSet();

    public EntityMapper(Class<T> entityClass) {
        this.entityName = entityClass.getCanonicalName();

        try {
            //该entity所有属性
            List<Field> fieldList = Lists.newArrayList();

            //本类字段
            Field[] fields = entityClass.getDeclaredFields();
            if (fields == null || fields.length == INT_0) {
                throw new DaoException(getEntityName() + " have no property");
            }
            fieldList.addAll(Arrays.asList(fields));

            // All the Super Classes.
            List<Class<?>> superClassList = DaoHelper.getTraversalSuperClass(entityClass);
            for(Class<?> cls : superClassList){
                fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
            }

            for (Field field : fieldList) {
                if (DaoHelper.isFinalOrStatic(field)) {
                    continue;
                }
                String propertyName = field.getName();
                //isTransient=false的加入到忽略持久化列表
                Column columnAnnotation = field.getAnnotation(Column.class);
                if (columnAnnotation != null && !columnAnnotation.isTransient()) {
                    notNeedTransientPropertySet.add(propertyName);
                    continue;
                }

                String columnName = DaoHelper.getColumnName(field);
                propertyToColumnMapper.put(propertyName, columnName);
                columnToPropertyMapper.put(columnName, propertyName);

            }
        } catch (Exception e) {
            throw new DaoException("无法创建Entity[" + getEntityName() + "]对应的EntityMapper", e);
        }
    }

    public String getEntityName() {
        return entityName;
    }

    public Map<String, String> getPropertyToColumnMapper() {
        return propertyToColumnMapper;
    }

    public Map<String, String> getColumnToPropertyMapper() {
        return columnToPropertyMapper;
    }

    public Set<String> getNotNeedTransientPropertySet() {
        return notNeedTransientPropertySet;
    }
}

