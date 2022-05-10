package com.ziroom.ferrari.repository.core.jdbc;

import com.google.common.collect.Lists;
import com.ziroom.ferrari.repository.core.DaoHelper;
import com.ziroom.ferrari.repository.core.DatabaseRouter;
import com.ziroom.ferrari.repository.core.IBaseDao;
import com.ziroom.ferrari.repository.core.annotation.Table;
import com.ziroom.ferrari.repository.core.constant.DBConstant;
import com.ziroom.ferrari.repository.core.constant.DialectEnum;
import com.ziroom.ferrari.repository.core.entity.IdEntity;
import com.ziroom.ferrari.repository.core.exception.DaoException;
import com.ziroom.ferrari.repository.core.query.*;
import java.lang.reflect.Field;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ziroom.ferrari.repository.core.constant.ApplicationConstant.*;
import static com.ziroom.ferrari.repository.core.jdbc.JdbcHelper.*;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:28
 * @Version 1.0
 */
public class JdbcBaseDao<T> implements IBaseDao<T> {

    private Class<T> entityClass;
    private EntityMapper<T> entityMapper;
    private JdbcSettings jdbcSettings;
    private DatabaseRouter router;

    public JdbcBaseDao(Class<T> entityClass, JdbcSettings jdbcSettings) {
        this.init(entityClass, jdbcSettings);
    }

    @Override
    public Class<T> getGenericClass() {
        return this.entityClass;
    }

    @Override
    public boolean exists(Serializable id) throws DaoException {
        return null != this.findOne(Arrays.asList(DBConstant.PK_NAME), Criteria.where(DBConstant.PK_NAME, id));
    }

    @Override
    public boolean exists(Criteria criteria) throws DaoException {
        return null != this.findOne(Arrays.asList(DBConstant.PK_NAME), criteria);
    }

    @Override
    public long countByCriteria(Criteria criteria) throws DaoException {
        List<Object> valueList = Lists.newArrayList();
        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_COUNT());
        sql.append(FROM(entityClass));
        sql.append(WHERE(criteria, valueList, entityMapper));

        return ((JdbcTemplate) router.readRoute()).queryForObject(sql.toString(), valueList.toArray(), Long.class);
    }

    @Override
    public long countAll() throws DaoException {
        StringBuilder sql = new StringBuilder();
        sql.append(SELECT_COUNT());
        sql.append(FROM(entityClass));
        return ((JdbcTemplate) router.readRoute()).queryForObject(sql.toString(), Long.class);
    }

    @Override
    public long countBySql(String sql, Map<String, Object> param) throws DaoException {
        return ((JdbcTemplate) router.readRoute()).queryForObject(sql, param.values().toArray(), Long.class);
    }

    @Override
    public T findOneById(Serializable id) throws DaoException {
        return this.findOne(Criteria.where(DBConstant.PK_NAME, id));
    }

    @Override
    public T findOneByQuery(Query query) throws DaoException {
        query.offset(INT_0).limit(INT_1);
        List<T> entityList = this.findListByQuery(query);
        return CollectionUtils.isEmpty(entityList) ? null : entityList.get(INT_0);
    }

    @Override
    public T findOneBySql(String sqlOrgin, Map<String, Object> param) throws DaoException {
        Query query = Query.query();
        query.offset(INT_0).limit(INT_1);
        StringBuilder sql = new StringBuilder(sqlOrgin);
        sql.append(LIMIT(query.getOffset(), query.getLimit(), jdbcSettings.getDialectEnum(), sql));
        List<T> entityList = this.findListBySql(sql.toString(), param);
        return CollectionUtils.isEmpty(entityList) ? null : entityList.get(INT_0);
    }

    @Override
    public List<T> findListByIds(List<Serializable> ids) throws DaoException {
        return this.findList(Criteria.where(DBConstant.PK_NAME, CriteriaOperators.IN, ids));
    }

    @Override
    public List<T> findListByQuery(Query query) throws DaoException {
        List<Object> valueList = Lists.newArrayList();

        StringBuilder sql = new StringBuilder();
        sql.append(SELECT(query, entityMapper));
        sql.append(FROM(entityClass));
        sql.append(WHERE(query.getCriteria(), valueList, entityMapper));
        sql.append(GROUP_BY(query.getGroupBys(), entityMapper));
        sql.append(ORDER_BY(query.getOrderBys(), entityMapper));
        sql.append(LIMIT(query.getOffset(), query.getLimit(), jdbcSettings.getDialectEnum(), sql));


        List<Map<String, Object>> list = ((JdbcTemplate) router.readRoute()).queryForList(sql.toString(), valueList.toArray());
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> entityList = Lists.newArrayList();
        for (Map<String, Object> map : list) {
            entityList.add(map2Entity(map, entityMapper, entityClass));
        }
        return entityList;
    }

    @Override
    public List<T> findListByQuery(Query query, Pageable pageable) throws DaoException {
        int limit = pageable.getPageSize();
        int offset = (pageable.getPageNumber() - INT_1) * limit;
        query.offset(offset).limit(limit);
        return this.findListByQuery(query);
    }

    @Override
    public List<T> findListBySql(String sql, Map<String, Object> param) throws DaoException {


        List<Map<String, Object>> list = ((JdbcTemplate) router.readRoute()).queryForList(sql, param.values().toArray());
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> entityList = Lists.newArrayList();
        for (Map<String, Object> map : list) {
            entityList.add(map2Entity(map, entityMapper, entityClass));
        }
        return entityList;
    }

    /**
     * todo 未开发完成，暂时不开放
     * @param entityList
     * @return
     * @throws DaoException
     */
    private int batchInsert(List<T> entityList) throws DaoException {
        if (entityList == null || entityList.size() == 0) {
            return 0;
        }
        // 不支持oracle
        if (DialectEnum.ORACLE.equals(jdbcSettings.getDialectEnum())) {
            return 0;
        }

        final IdEntity idEntity = (IdEntity) entityList.get(0);
        Class beanClass = idEntity.getClass();
        Field[] fields = beanClass.getDeclaredFields();
        List<Field> fieldList = Lists.newArrayList();
        fieldList.addAll(Arrays.asList(fields));

        final List<Object> valueList = Lists.newArrayList();

        String insert = INSERT(idEntity, valueList, entityMapper, entityClass, jdbcSettings.getDialectEnum(), null);

        PreparedStatement[] preparedStatements = new PreparedStatement[1];

        ((JdbcTemplate) router.writeRoute()).batchUpdate(insert, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                final IdEntity idEntity = (IdEntity) entityList.get(i);;


                for (Field field : fieldList) {
                    String propertyName = field.getName();
                    // Ignore fields by final, static, and primary key.
                    if (DaoHelper.isFinalOrStatic(field)) {
                        continue;
                    }

                    if (propertyName.equals(DBConstant.PK_NAME) && idEntity.getId() > LONG_0) {
                        try {
                            valueList.add(DaoHelper.getColumnValue(IdEntity.class.getDeclaredField(DBConstant.PK_NAME), idEntity));
                        } catch (NoSuchFieldException e) {
                            throw new DaoException("insert error", e);
                        }
                    }

                    // Ignore fields by isTransient=false which have been set in notNeedTransientPropertySet.
                    if (entityMapper.getNotNeedTransientPropertySet().contains(propertyName)) {
                        continue;
                    }

                    valueList.add(DaoHelper.getColumnValue(field, idEntity));
                }

                int index = INT_0;
                for (Object value : valueList) {
                    StatementCreatorUtils.setParameterValue(preparedStatement, ++index, SqlTypeValue.TYPE_UNKNOWN, value);
                }
            }

            @Override
            public int getBatchSize() {
                return entityList.size();
            }
        });

        return entityList.size();
    }

    @Override
    public int insert(T entity) throws DaoException {
        final IdEntity idEntity = (IdEntity) entity;
        final List<Object> valueList = Lists.newArrayList();
        PreparedStatementCreator psc = new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                String insertSqlToUse = INSERT(idEntity, valueList, entityMapper, entityClass, jdbcSettings.getDialectEnum(), connection);
                PreparedStatement ps;
                if (idEntity.getId() > LONG_0) {
                    ps = connection.prepareStatement(insertSqlToUse);
                } else {
                    ps = connection.prepareStatement(insertSqlToUse, new String[]{DBConstant.PK_NAME});
                }

                int i = INT_0;
                for (Object value : valueList) {
                    StatementCreatorUtils.setParameterValue(ps, ++i, SqlTypeValue.TYPE_UNKNOWN, value);
                }
                return ps;
            }
        };

        int n;
        if (idEntity.getId() > LONG_0 || DialectEnum.ORACLE.equals(jdbcSettings.getDialectEnum())) {//KeyHolder不支持oracle
            n = ((JdbcTemplate) router.writeRoute()).update(psc);
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            n = ((JdbcTemplate) router.writeRoute()).update(psc, keyHolder);
            idEntity.setId(keyHolder.getKey().longValue());
        }
        return n;
    }

    @Override
    public int update(T entity) throws DaoException {
        return this.update(entity, null);
    }

    @Override
    public int update(T entity, List<String> propetyList) throws DaoException {
        return this.updateById(((IdEntity) entity).getId(), DaoHelper.entity2UpdateV2(entity, propetyList));
    }

    @Override
    public int updateById(Serializable id, Update update) throws DaoException {
        return this.updateByCriteria(Criteria.where(DBConstant.PK_NAME, id), update);
    }

    @Override
    public int updateByIds(List<Serializable> ids, Update update) throws DaoException {
        return this.updateByCriteria(Criteria.where(DBConstant.PK_NAME, CriteriaOperators.IN, ids), update);
    }

    @Override
    public int updateByCriteria(Criteria criteria, Update update) throws DaoException {
        List<Object> valueList = Lists.newArrayList();

        StringBuilder sql = new StringBuilder();
        sql.append(UPDATE(entityClass));
        sql.append(SET(update, valueList, entityMapper));
        sql.append(WHERE(criteria, valueList, entityMapper));

        return ((JdbcTemplate) router.writeRoute()).update(sql.toString(), valueList.toArray());
    }

    @Override
    public int updateBySql(String sql, Map<String, Object> param) throws DaoException {

        return ((JdbcTemplate) router.writeRoute()).update(sql, param.values().toArray());
    }

    @Override
    public int deleteById(Serializable id, String operator) throws DaoException {
        StringBuilder sql = new StringBuilder();
        sql.append(DELETE(entityClass));
        return ((JdbcTemplate) router.writeRoute()).update(sql.toString(), new Object[]{id});
    }

    @Override
    public T findOne(List<String> fields, Criteria criteria) throws DaoException {
        Query query = Query.query(criteria);
        query.includeField(fields.toArray(new String[fields.size()]));
        return this.findOneByQuery(query);
    }

    @Override
    public T findOne(Criteria criteria) throws DaoException {
        Query query = Query.query(criteria);
        return this.findOneByQuery(query);
    }

    @Override
    public List<T> findList(List<String> fields, Criteria criteria) throws DaoException {
        Query query = Query.query(criteria);
        query.includeField(fields.toArray(new String[fields.size()]));
        return this.findListByQuery(query);
    }

    @Override
    public List<T> findList(List<String> fields, Criteria criteria, List<OrderBy> orderBys) throws DaoException {
        Query query = Query.query(criteria);
        query.includeField(fields.toArray(new String[fields.size()]));
        query.withOrderBy(orderBys.toArray(new OrderBy[orderBys.size()]));
        return this.findListByQuery(query);
    }

    @Override
    public List<T> findList(List<String> fields, Criteria criteria, List<OrderBy> orderBys, Pageable pageable) throws DaoException {
        Query query = Query.query(criteria);
        query.includeField(fields.toArray(new String[fields.size()]));
        query.withOrderBy(orderBys.toArray(new OrderBy[orderBys.size()]));
        return this.findListByQuery(query, pageable);
    }

    @Override
    public List<T> findList(Criteria criteria) throws DaoException {
        Query query = Query.query(criteria);
        return this.findListByQuery(query);
    }

    @Override
    public List<T> findList(Criteria criteria, List<OrderBy> orderBys) throws DaoException {
        Query query = Query.query(criteria);
        query.withOrderBy(orderBys.toArray(new OrderBy[orderBys.size()]));
        return this.findListByQuery(query);
    }

    @Override
    public List<T> findList(Criteria criteria, List<OrderBy> orderBys, Pageable pageable) throws DaoException {
        Query query = Query.query(criteria);
        query.withOrderBy(orderBys.toArray(new OrderBy[orderBys.size()]));
        return this.findListByQuery(query, pageable);
    }

    @Override
    public List<T> findAllList() throws DaoException {
        Query query = Query.query();
        return this.findListByQuery(query);
    }

    @Override
    public List<T> findAllList(List<String> fields) throws DaoException {
        Query query = Query.query();
        query.includeField(fields.toArray(new String[fields.size()]));
        return this.findListByQuery(query);
    }

    @Override
    public List<T> findAllList(List<String> fields, List<OrderBy> orderBys) throws DaoException {
        Query query = Query.query();
        query.includeField(fields.toArray(new String[fields.size()]));
        query.withOrderBy(orderBys.toArray(new OrderBy[orderBys.size()]));
        return this.findListByQuery(query);
    }

    @Override
    public List<T> findAllList(List<String> fields, List<OrderBy> orderBys, Pageable pageable) throws DaoException {
        Query query = Query.query();
        query.includeField(fields.toArray(new String[fields.size()]));
        query.withOrderBy(orderBys.toArray(new OrderBy[orderBys.size()]));
        return this.findListByQuery(query, pageable);
    }

    private void init(Class<T> entityClass, JdbcSettings jdbcSettings) {
        //表名
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        if (tableAnnotation == null) {
            throw new DaoException(entityClass.getName() + " 表注解(Table) 必须指定");
        }
        if (jdbcSettings == null) {
            throw new DaoException(entityClass.getName() + " jdbcSettings必须配置");
        }
        //create router
        JdbcTemplateRouterFactory.INSTANCE.setDatabaseRouter(jdbcSettings);

        this.entityClass = entityClass;
        this.jdbcSettings = jdbcSettings;
        this.entityMapper = new EntityMapper<T>(entityClass);
        this.router = JdbcTemplateRouterFactory.INSTANCE.getDatabaseRouter(jdbcSettings);
    }
}