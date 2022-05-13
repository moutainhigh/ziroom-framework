package com.ziroom.ferrari.repository.core;

import com.google.common.base.Preconditions;
import com.ziroom.ferrari.repository.core.annotation.DaoDescription;
import com.ziroom.ferrari.repository.core.constant.OrmFrameEnum;
import com.ziroom.ferrari.repository.core.entity.IdEntity;
import com.ziroom.ferrari.repository.core.entity.StringIdEntity;
import com.ziroom.ferrari.repository.core.exception.DaoException;
import com.ziroom.ferrari.repository.core.exception.DaoExceptionTranslator;
import com.ziroom.ferrari.repository.core.exception.DaoMethodParameterException;
import com.ziroom.ferrari.repository.core.interceptor.InterceptorChain;
import com.ziroom.ferrari.repository.core.jdbc.JdbcBaseDao;
import com.ziroom.ferrari.repository.core.jdbc.JdbcSettings;
import com.ziroom.ferrari.repository.core.query.*;
import com.ziroom.ferrari.repository.core.util.FerrariStringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.ziroom.ferrari.repository.core.constant.ApplicationConstant.INT_0;
import static com.ziroom.ferrari.repository.core.constant.ApplicationConstant.LONG_0;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:12
 * @Version 1.0
 */
public abstract class BaseDao<T> implements IBaseDao<T>, ApplicationContextAware {

    /**
     * 泛型的entity类型
     */
    private Class<T> entityClass;

    /**
     * 拦截器链
     */
    @Autowired(required = false)
    private InterceptorChain interceptorChain;

    /**
     * dao代理
     */
    private IBaseDao<T> baseDaoProxy;

    private ApplicationContext applicationContext;

    @Override
    public Class<T> getGenericClass() {
        return this.entityClass;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public boolean exists(Serializable id) throws DaoException {
        try {
            checkArgumentId(id);
            return baseDaoProxy.exists(id);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public boolean exists(Criteria criteria) throws DaoException {
        try {
            checkArgumentCriteria(criteria);
            return baseDaoProxy.exists(criteria);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public long countByCriteria(Criteria criteria) throws DaoException {
        try {
            checkArgumentCriteria(criteria);
            return baseDaoProxy.countByCriteria(criteria);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public long countAll() throws DaoException {
        try {
            return baseDaoProxy.countAll();
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public long countBySql(String sql, Map<String, Object> param) throws DaoException {
        try {
            checkArgument(sql, param);
            return baseDaoProxy.countBySql(sql, param);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public T findOneById(Serializable id) throws DaoException {
        try {
            checkArgumentId(id);
            return baseDaoProxy.findOneById(id);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public T findOneByQuery(Query query) throws DaoException {
        try {
            checkArgumentQuery(query);
            return baseDaoProxy.findOneByQuery(query);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public T findOneBySql(String sql, Map<String, Object> param) throws DaoException {
        try {
            checkArgument(sql, param);
            return baseDaoProxy.findOneBySql(sql, param);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findListByIds(List<Serializable> ids) throws DaoException {
        try {
            checkArgumentIds(ids);
            return baseDaoProxy.findListByIds(ids);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findListByQuery(Query query) throws DaoException {
        try {
            checkArgumentQuery(query);
            return baseDaoProxy.findListByQuery(query);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findListByQuery(Query query, Pageable pageable) throws DaoException {
        try {
            checkArgumentQuery(query);
            checkArgumentPageable(pageable);
            return baseDaoProxy.findListByQuery(query, pageable);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findListBySql(String sql, Map<String, Object> param) throws DaoException {
        try {
            checkArgument(sql, param);
            return baseDaoProxy.findListBySql(sql, param);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int insert(T entity) throws DaoException {
        try {
            checkArgumentEntity(entity);
            return baseDaoProxy.insert(entity);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int batchInsert(List<T> entityList) throws DaoException {
        try {
            checkArgumentEntity(entityList);
            return baseDaoProxy.batchInsert(entityList);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int update(T entity) throws DaoException {
        try {
            checkArgumentEntity(entity);
            return baseDaoProxy.update(entity);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int update(T entity, List<String> propetyList) throws DaoException {
        try {
            checkArgumentEntity(entity);
            checkArgumentFields(propetyList);
            return baseDaoProxy.update(entity, propetyList);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int updateById(Serializable id, Update update) throws DaoException {
        try {
            checkArgumentId(id);
            checkArgumentUpdate(update);
            return baseDaoProxy.updateById(id, update);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int updateByIds(List<Serializable> ids, Update update) throws DaoException {
        try {
            checkArgumentIds(ids);
            checkArgumentUpdate(update);
            return baseDaoProxy.updateByIds(ids, update);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int updateByCriteria(Criteria criteria, Update update) throws DaoException {
        try {
            checkArgumentCriteria(criteria);
            checkArgumentUpdate(update);
            return baseDaoProxy.updateByCriteria(criteria, update);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int updateBySql(String sql, Map<String, Object> param) throws DaoException {
        try {
            checkArgument(sql, param);
            return baseDaoProxy.updateBySql(sql, param);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public int deleteById(Serializable id, String operator) throws DaoException {
        try {
            checkArgumentId(id);
            Preconditions.checkArgument(FerrariStringUtils.isNotBlank(operator), "operator must be not null or empty");
            return baseDaoProxy.deleteById(id, operator);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public T findOne(List<String> fields, Criteria criteria) throws DaoException {
        try {
            checkArgumentFields(fields);
            checkArgumentCriteria(criteria);
            return baseDaoProxy.findOne(fields, criteria);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public T findOne(Criteria criteria) throws DaoException {
        try {
            checkArgumentCriteria(criteria);
            return baseDaoProxy.findOne(criteria);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findList(List<String> fields, Criteria criteria) throws DaoException {
        try {
            checkArgumentFields(fields);
            checkArgumentCriteria(criteria);
            return baseDaoProxy.findList(fields, criteria);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findList(List<String> fields, Criteria criteria, List<OrderBy> orderBys) throws DaoException {
        try {
            checkArgumentFields(fields);
            checkArgumentCriteria(criteria);
            checkArgumentOrderBys(orderBys);
            return baseDaoProxy.findList(fields, criteria, orderBys);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findList(List<String> fields, Criteria criteria, List<OrderBy> orderBys, Pageable pageable) throws DaoException {
        try {
            checkArgumentFields(fields);
            checkArgumentCriteria(criteria);
            checkArgumentOrderBys(orderBys);
            checkArgumentPageable(pageable);
            return baseDaoProxy.findList(fields, criteria, orderBys, pageable);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findList(Criteria criteria) throws DaoException {
        try {
            checkArgumentCriteria(criteria);
            return baseDaoProxy.findList(criteria);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findList(Criteria criteria, List<OrderBy> orderBys) throws DaoException {
        try {
            checkArgumentCriteria(criteria);
            checkArgumentOrderBys(orderBys);
            return baseDaoProxy.findList(criteria, orderBys);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findList(Criteria criteria, List<OrderBy> orderBys, Pageable pageable) throws DaoException {
        try {
            checkArgumentCriteria(criteria);
            checkArgumentOrderBys(orderBys);
            checkArgumentPageable(pageable);
            return baseDaoProxy.findList(criteria, orderBys, pageable);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findAllList() throws DaoException {
        try {
            return baseDaoProxy.findAllList();
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findAllList(List<String> fields) throws DaoException {
        try {
            checkArgumentFields(fields);
            return baseDaoProxy.findAllList(fields);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findAllList(List<String> fields, List<OrderBy> orderBys) throws DaoException {
        try {
            checkArgumentFields(fields);
            checkArgumentOrderBys(orderBys);
            return baseDaoProxy.findAllList(fields, orderBys);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @Override
    public List<T> findAllList(List<String> fields, List<OrderBy> orderBys, Pageable pageable) throws DaoException {
        try {
            checkArgumentFields(fields);
            checkArgumentOrderBys(orderBys);
            checkArgumentPageable(pageable);
            return baseDaoProxy.findAllList(fields, orderBys, pageable);
        } catch (RuntimeException e) {
            throw DaoExceptionTranslator.translate(e);
        }
    }

    @PostConstruct
    protected void afterPropertiesSet() {
        //设置dao具体类class
        Class daoClass = this.getClass();
        //设置泛型entityClass
        Type t = daoClass.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            this.entityClass = (Class<T>) p[INT_0];
        }
        if (this.entityClass == null) {
            throw new DaoException("can not get the entity's Generic Type");
        }

        //得到dao注解描述信息 给默认值考虑兼容业务之前代码
        OrmFrameEnum ormFrameEnum = OrmFrameEnum.MYBATIS;
        String settingBean = "masterSqlSessionTemplate";
        DaoDescription daoDescription = (DaoDescription) daoClass.getAnnotation(DaoDescription.class);
        if (daoDescription != null) {
            ormFrameEnum = daoDescription.ormFrame();
            settingBean = daoDescription.settingBean();
        }

        if (OrmFrameEnum.MYBATIS == ormFrameEnum) {
//            checkArgumentEntityClass(ormFrameEnum, this.entityClass);
//
//            MybatisSettings mybatisSettings = null;
//            /**
//             * 兼容以前mybatis不设置DaoDescription注解的代码
//             */
//            Object bean = this.getBeanFromSpringContext(settingBean);
//            if (bean instanceof SqlSessionTemplate) {
//                mybatisSettings = new MybatisSettings();
//                mybatisSettings.setWriteSqlSessionTemplates(Lists.newArrayList((SqlSessionTemplate) bean));
//                try {
//                    Object beanS = this.getBeanFromSpringContext("slaveSqlSessionTemplate");
//                    mybatisSettings.setReadSqlSessionTemplates(Lists.newArrayList((SqlSessionTemplate) beanS));
//                } catch (RuntimeException e) {
//                    ApplicationLogger.error(e);
//                }
//            } else if (bean instanceof MybatisSettings) {
//                mybatisSettings = (MybatisSettings) bean;
//            }
//
//            MybatisBaseDao<T> mybatisBaseDao = new MybatisBaseDao<T>(this.entityClass, mybatisSettings);
//            mybatisBaseDao.setNameSpace(daoClass.getName());
//            this.setBaseDaoProxy(mybatisBaseDao);
        } else if (OrmFrameEnum.JDBC_TEMPLATE == ormFrameEnum) {
            checkArgumentEntityClass(ormFrameEnum, this.entityClass);
            JdbcBaseDao<T> jdbcBaseDao = new JdbcBaseDao<T>(this.entityClass, (JdbcSettings) this.getBeanFromSpringContext(settingBean));
            this.setBaseDaoProxy(jdbcBaseDao);
        } else if (OrmFrameEnum.ELASTICSEARCH == ormFrameEnum) {
//            checkArgumentEntityClass(ormFrameEnum, this.entityClass);
//            ElasticSearchBaseDao<T> elasticSearchBaseDao = new ElasticSearchBaseDao<T>(this.entityClass, (ElasticSearchSettings) this.getBeanFromSpringContext(settingBean));
//            this.setBaseDaoProxy(elasticSearchBaseDao);
        } else {
            throw new DaoException("Unsupported ORM framework");
        }
    }


    /**
     * 关系数据库只能继承IdEntity
     * 非关系数据库都可以继承
     *
     * @param ormFrameEnum
     * @param entityClass
     */
    private static void checkArgumentEntityClass(OrmFrameEnum ormFrameEnum, Class entityClass) {
        if (OrmFrameEnum.MYBATIS == ormFrameEnum || OrmFrameEnum.JDBC_TEMPLATE == ormFrameEnum) {
            Preconditions.checkArgument(IdEntity.class.isAssignableFrom(entityClass), "entity[" + entityClass.getName() + "] must inherit IdEntity");
        } else if (OrmFrameEnum.ELASTICSEARCH == ormFrameEnum) {
            Preconditions.checkArgument(IdEntity.class.isAssignableFrom(entityClass) ||
                    StringIdEntity.class.isAssignableFrom(entityClass), "entity[" + entityClass.getName() + "] must inherit IdEntity or StringIdEntity");
        }
    }

    /**
     * 校验query
     */
    private static void checkArgumentCriteria(Criteria criteria) {
        Preconditions.checkArgument(criteria != null, "Param criteria must be not null");
    }

    /**
     * 校验query
     */
    private static void checkArgumentQuery(Query query) {
        Preconditions.checkArgument(query != null, "Param query must be not null");
    }

    /**
     * 校验id
     */
    private static void checkArgumentId(Serializable id) {
        if (id instanceof Long) {
            Preconditions.checkArgument((Long) id > LONG_0, "Param id must be greater than 0");
        } else if (id instanceof String) {
            Preconditions.checkArgument(FerrariStringUtils.isNotBlank((String) id), "Param id must be not empty");
        } else {
            throw new DaoMethodParameterException("Param id's type must be long or String");
        }

    }

    /**
     * 校验ids
     */
    private static void checkArgumentIds(List<Serializable> ids) {
        Preconditions.checkArgument(ids != null && ids.size() > INT_0, "Param ids must be not null and empty");
        checkArgumentId(ids.get(0));
    }

    /**
     * 校验ids
     */
    private static void checkArgumentPageable(Pageable pageable) {
        Preconditions.checkArgument(pageable != null, "Param pageable must be not null and empty");
    }

    /**
     * 校验fields
     */
    private static void checkArgumentFields(List<String> fields) {
        Preconditions.checkArgument(fields != null && fields.size() > INT_0, "Param fields must be not null");
    }

    /**
     * 校验orderBys
     */
    private static void checkArgumentOrderBys(List<OrderBy> orderBys) {
        Preconditions.checkArgument(orderBys != null && orderBys.size() > INT_0, "Param orderBys must be not null");
    }

    /**
     * 查询前的校验
     */
    private static void checkArgument(String sql, Map<String, Object> param) {
        Preconditions.checkArgument(FerrariStringUtils.isNotBlank(sql), "Param sql must be not null and empty");
        Preconditions.checkArgument(param != null && !param.isEmpty(), "Param param must be not null and empty");
    }

    /**
     * 更新操作前的校验
     */
    private static void checkArgumentEntity(Object entity) {
        Preconditions.checkArgument(entity != null, "Param update must be not null");
    }

    /**
     * 更新操作前的校验
     */
    private static void checkArgumentUpdate(Update update) {
        Preconditions.checkArgument(update != null, "Param update must be not null");
        Preconditions.checkArgument(!update.getSetMap().isEmpty(), "Param update must be set");
    }


    private Object getBeanFromSpringContext(String beanName) {
        return this.applicationContext.getBean(beanName);
    }

    public void setBaseDaoProxy(IBaseDao<T> baseDaoProxy) {
        if (this.interceptorChain != null) {
            this.baseDaoProxy = (IBaseDao) interceptorChain.pluginAll(baseDaoProxy);
        } else {
            this.baseDaoProxy = baseDaoProxy;
        }
    }
}
