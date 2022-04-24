package com.ziroom.ferrari.repository.core.jdbc;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ziroom.ferrari.repository.core.DaoHelper;
import com.ziroom.ferrari.repository.core.annotation.Table;
import com.ziroom.ferrari.repository.core.constant.DBConstant;
import com.ziroom.ferrari.repository.core.constant.DialectEnum;
import com.ziroom.ferrari.repository.core.entity.IdEntity;
import com.ziroom.ferrari.repository.core.exception.DaoException;
import com.ziroom.ferrari.repository.core.query.*;
import com.ziroom.ferrari.repository.core.util.BeanUtils;
import com.ziroom.ferrari.repository.core.util.FerrariStringUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.ziroom.ferrari.repository.core.constant.ApplicationConstant.*;
import static com.ziroom.ferrari.repository.core.constant.SymbolConstant.*;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:30
 * @Version 1.0
 */
public final class JdbcHelper {
    private static final String DEBUG_SQL_PREFIX = "==========JdbcBaseDao Generate SQL:";

    private JdbcHelper() {
    }

    static String SELECT_COUNT() {
        return "SELECT COUNT(*) ";
    }

    static String SELECT(Query query, EntityMapper<?> entityMapper) {
        Map<String, String> propertyToColumnMapper = entityMapper.getPropertyToColumnMapper();
        List<String> fields = query.getFields();
        List<GroupBy> groupBys = query.getGroupBys();

        StringBuilder sb = new StringBuilder("SELECT ");
        if (CollectionUtils.isEmpty(fields)) {
            if (CollectionUtils.isEmpty(groupBys)) {
                for (String column : propertyToColumnMapper.values()) {
                    sb.append(column).append(COMMA);
                }
            } else {
                for (GroupBy groupBy : groupBys) {
                    String groupByKey = groupBy.getKey();
                    String groupCountAlias = groupBy.getGroupCountAlias();
                    sb.append(groupByKey).append(COMMA);
                    if (FerrariStringUtils.isNotBlank(groupCountAlias)) {   //需要每组数量groupCountAlias是entity中定义的非持久化字段
                        sb.append("COUNT(*) AS " + propertyToColumnMapper.get(groupCountAlias)).append(COMMA);
                    }
                }
            }
        } else {
            for (String field : fields) {
                String column = propertyToColumnMapper.get(field);
                if (!CollectionUtils.isEmpty(groupBys)) {
                    for (GroupBy groupBy : groupBys) {
                        String groupCountAlias = groupBy.getGroupCountAlias();
                        if (FerrariStringUtils.isNotBlank(groupCountAlias) && field.equals(groupCountAlias)) {   //需要每组数量groupCountAlias是entity中定义的非持久化字段
                            column = "COUNT(*) AS " + column;
                        }
                    }
                }
                sb.append(column).append(COMMA);
            }
        }
        sb.deleteCharAt(sb.length() - INT_1);//去掉最后一个,
        sb.append(BLANK);
        return sb.toString();
    }

    static String DELETE(Class<?> entityClass) {
        return "DELETE " + FROM(entityClass) + " WHERE id = ?";
    }

    static String FROM(Class<?> entityClass) {
        return "FROM " + getTableName(entityClass) + BLANK;
    }

    static String GROUP_BY(List<GroupBy> groupByList, EntityMapper entityMapper) {
        if (CollectionUtils.isEmpty(groupByList)) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder("GROUP BY ");
        for (GroupBy groupBy : groupByList) {
            sb.append(entityMapper.getPropertyToColumnMapper().get(groupBy.getKey())).append(COMMA);
        }
        sb.deleteCharAt(sb.length() - INT_1);//去掉最后一个,
        sb.append(BLANK);
        return sb.toString();
    }

    static String ORDER_BY(List<OrderBy> orderByList, EntityMapper entityMapper) {
        if (CollectionUtils.isEmpty(orderByList)) {
            return EMPTY;
        }

        StringBuilder sb = new StringBuilder("ORDER BY ");
        for (OrderBy orderBy : orderByList) {
            sb.append(entityMapper.getPropertyToColumnMapper().get(orderBy.getKey())).append(BLANK).append(orderBy.getDirection()).append(COMMA);
        }
        sb.deleteCharAt(sb.length() - INT_1);//去掉最后一个,
        sb.append(BLANK);
        return sb.toString();
    }

    static String LIMIT(int offset, int limit, DialectEnum dialectEnum, final StringBuilder sql) {
        if (offset < INT_0 || limit <= INT_0) {
            return EMPTY;
        }
        StringBuilder newSql = new StringBuilder();
        if (DialectEnum.MYSQL.equals(dialectEnum)) {
            newSql.append(sql.toString());
            newSql.append("LIMIT ");
            newSql.append(offset).append(COMMA).append(limit);
        } else if (DialectEnum.ORACLE.equals(dialectEnum)) {
            newSql.append("SELECT * FROM (")
                    .append("SELECT ROWNUM AS RN,table_alias.* FROM (")
                    .append(sql.toString())
                    .append(") table_alias ")
                    .append("WHERE ROWNUM <= ")
                    .append(offset + limit)
                    .append(") ")
                    .append("WHERE RN > ").append(offset);

        }
        sql.setLength(INT_0);
        return newSql.toString();
    }

    static String UPDATE(Class<?> entityClass) {
        return "UPDATE " + getTableName(entityClass) + BLANK;
    }

    static String SET(Update update, List<Object> valueList, EntityMapper<?> entityMapper) {
        Map<String, String> propertyToColumnMapper = entityMapper.getPropertyToColumnMapper();
        Set<String> notNeedTransientPropertySet = entityMapper.getNotNeedTransientPropertySet();

        StringBuilder sb = new StringBuilder("SET ");
        Set<String> keySet = update.getSetMap().keySet();
        for (String key : keySet) {
            if (notNeedTransientPropertySet.contains(key)) {
                continue;
            }
            sb.append(propertyToColumnMapper.get(key)).append("=?,");
            valueList.add(update.get(key));
        }
        sb.deleteCharAt(sb.length() - INT_1);//去掉最后一个,
        sb.append(BLANK);
        return sb.toString();
    }

    static <T> T map2Entity(Map<String, Object> map, EntityMapper<T> entityMapper, Class<T> entityClass) {
        Map<String, String> columnToPropertyMapper = entityMapper.getColumnToPropertyMapper();

        try {
            Set<String> keySet = map.keySet();
            HashMap<String, Object> propertyMap = Maps.newHashMapWithExpectedSize(keySet.size());
            for (String key : keySet) {
                //如果select出来的字段有而entity没有对应的属性，需要忽略该字段
                String propertyName = columnToPropertyMapper.get(key.toLowerCase());
                if (propertyName == null) {
                    continue;
                }
                propertyMap.put(propertyName, map.get(key));
            }
            return BeanUtils.mapToBean(propertyMap, entityClass);
        } catch (Exception e) {
            throw new DaoException("map2Entity无法将数据库中行记录[" + map + "]转换成Entity对象[" + entityClass.getSimpleName() + "]", e);
        }
    }

    /**
     * 根据criteria拼装sql where
     *
     * @param criteria     - 条件对象
     * @param valueList    - 值列表
     * @param entityMapper - entityMapper
     * @return where sql
     */
    static String WHERE(Criteria criteria, List<Object> valueList, EntityMapper<?> entityMapper) {
        if (criteria == null) {
            return EMPTY;
        }

        Map<String, String> propertyToColumnMapper = entityMapper.getPropertyToColumnMapper();

        StringBuilder whereSB = new StringBuilder();
        //criteria一定不为null，且criteria.getCriteriaChain()一定不为empty
        List<Criteria> criterias = criteria.getCriteriaChain();
        for (Criteria c : criterias) {
            if (CriteriaOperators.isNoValueOperator(c.getOperator())) {
                whereSB.append(" AND ").append(propertyToColumnMapper.get(c.getKey())).append(BLANK).append(c.getOperator());
            } else if (CriteriaOperators.isSingleValueOperator(c.getOperator())) {
                whereSB.append(" AND ").append(propertyToColumnMapper.get(c.getKey())).append(BLANK).append(c.getOperator()).append(" ?");
                valueList.add(c.getValue());
            } else if (CriteriaOperators.isMultiValueOperator(c.getOperator())) {
                whereSB.append(" AND ").append(propertyToColumnMapper.get(c.getKey())).append(BLANK).append(c.getOperator()).append(" (");
                Collection collection = (Collection) c.getValue();
                for (Object item : collection) {
                    whereSB.append(QUESTION).append(COMMA);
                    valueList.add(item);
                }
                whereSB.deleteCharAt(whereSB.length() - INT_1);
                whereSB.append(")");
            }
        }
        String whereSql = "WHERE " + whereSB.delete(INT_0, INT_5).toString() + BLANK;//去掉第一个 and
        return whereSql;
    }

    /**
     * 生成oracle id
     *
     * @param sequence   - sequence名字
     * @param connection - 链接
     * @return - id
     */
    private static long genOracleId(String sequence, Connection connection) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT " + sequence + ".NEXTVAL AS ID FROM DUAL");
            rs = pstmt.executeQuery();
            rs.next();
            return rs.getLong(INT_1);
        } catch (SQLException e) {
            throw new DaoException("无法获取oracle的sequence:" + sequence);
        } finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        }
    }

    static String INSERT(IdEntity idEntity, List<Object> valueList, EntityMapper<?> entityMapper, Class<?> entityClass, DialectEnum dialectEnum, Connection connection) {
        Map<String, String> propertyToColumnMapper = entityMapper.getPropertyToColumnMapper();
        Set<String> notNeedTransientPropertySet = entityMapper.getNotNeedTransientPropertySet();

        StringBuilder sb = new StringBuilder(" INSERT INTO ");

        //组装表名
        Class beanClass = idEntity.getClass();
        sb.append(getTableName(entityClass)).append(" (");

        //组装字段
        List<Field> fieldList = Lists.newArrayList();
        List<String> pList = Lists.newArrayList();

        //IdEntity字段
        if (idEntity.getId() > LONG_0) {
            pList.add(QUESTION);
            sb.append(propertyToColumnMapper.get(DBConstant.PK_NAME)).append(COMMA);
            try {
                valueList.add(DaoHelper.getColumnValue(IdEntity.class.getDeclaredField(DBConstant.PK_NAME), idEntity));
            } catch (NoSuchFieldException e) {
                throw new DaoException("insert error", e);
            }
        } else {
            if (DialectEnum.ORACLE.equals(dialectEnum)) {
                sb.append(propertyToColumnMapper.get(DBConstant.PK_NAME)).append(COMMA);
                if (FerrariStringUtils.isNotBlank(getSequenceName(entityClass))) {
                    pList.add(QUESTION);
                    long id = genOracleId(getSequenceName(entityClass), connection);
                    valueList.add(id);
                    idEntity.setId(id);
                } else {
                    throw new DaoException("连接ORACLE,实体Table注解必须设置sequence");
                }
            }
        }

        //--Modified to considering unrestricted super class by mxj 20180619.
        // Get local fields of the entity.
        Field[] fields = beanClass.getDeclaredFields();
        fieldList.addAll(Arrays.asList(fields));

        // Get all fields of the Super Classes of the entity.
        // All the Super Classes.
        List<Class<?>> superClassList = DaoHelper.getTraversalSuperClass(entityClass);
        for (Class<?> cls : superClassList) {
            fieldList.addAll(Arrays.asList(cls.getDeclaredFields()));
        }

        for (Field field : fieldList) {
            String propertyName = field.getName();
            // Ignore fields by final, static, and primary key.
            if (DaoHelper.isFinalOrStatic(field) || propertyName.equals(DBConstant.PK_NAME)) {
                continue;
            }
            // Ignore fields by isTransient=false which have been set in notNeedTransientPropertySet.
            if (notNeedTransientPropertySet.contains(propertyName)) {
                continue;
            }

            sb.append(propertyToColumnMapper.get(propertyName)).append(COMMA);
            pList.add(QUESTION);
            valueList.add(DaoHelper.getColumnValue(field, idEntity));
        }
        sb.deleteCharAt(sb.length() - INT_1).append(")");

        //组装值
        sb.append(" VALUES (");
        for (String p : pList) {
            sb.append(p).append(COMMA);
        }
        sb.deleteCharAt(sb.length() - INT_1).append(") ");
        return sb.toString();
    }

    /**
     * 根据entity的class获取对应的表名
     *
     * @param entityClass - entityClass
     * @return - 注解标注的表名
     */
    static String getTableName(Class<?> entityClass) {
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        return tableAnnotation.value();
    }

    /**
     * 根据entity的class获取对应的sequence
     *
     * @param entityClass - entityClass
     * @return - 注解标注的sequence
     */
    static String getSequenceName(Class<?> entityClass) {
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        return tableAnnotation.sequence();
    }

    /**
     * 格式化执行的sql
     *
     * @param sql
     * @param valueList
     * @return
     */
    static String formatSql(String sql, final List<Object> valueList) {
        if (valueList == null || valueList.isEmpty()) {
            return sql.toString();
        }

        StringBuilder sb = new StringBuilder();
        int length = sql.length();
        int questionIndex = 0;
        for (int i = 0; i < length; i++) {
            char c = sql.charAt(i);
            if (c == '?') {
                Object value = valueList.get(questionIndex);
                if (value instanceof String) {
                    sb.append("'").append(value.toString()).append("'");
                } else {
                    sb.append(value.toString());
                }
                questionIndex++;
            } else {
                sb.append(c);
            }
        }
        return DEBUG_SQL_PREFIX + sb.toString();
    }

    /**
     * 格式化执行的sql
     *
     * @param sql
     * @return
     */
    static String formatSql(String sql) {
        return formatSql(sql, Collections.EMPTY_LIST);
    }

    /**
     * 格式化执行的sql
     *
     * @param sql
     * @return
     */
    static String formatSql(String sql, final Map<String, Object> param) {
        return formatSql(sql, Lists.newArrayList(param.values()));
    }
}
