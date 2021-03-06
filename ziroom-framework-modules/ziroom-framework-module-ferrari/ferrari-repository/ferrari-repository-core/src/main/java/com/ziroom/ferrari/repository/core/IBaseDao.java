package com.ziroom.ferrari.repository.core;

import com.ziroom.ferrari.repository.core.exception.DaoException;
import com.ziroom.ferrari.repository.core.query.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:15
 * @Version 1.0
 */
public interface IBaseDao<T> {

    Class<T> getGenericClass();

    boolean exists(Serializable id) throws DaoException;

    boolean exists(Criteria criteria) throws DaoException;

    long countByCriteria(Criteria criteria) throws DaoException;

    long countAll() throws DaoException;

    long countBySql(String sql, Map<String, Object> param) throws DaoException;

    T findOneById(Serializable id) throws DaoException;

    T findOneByQuery(Query query) throws DaoException;

    T findOneBySql(String sql, Map<String, Object> param) throws DaoException;

    List<T> findListByIds(List<Serializable> ids) throws DaoException;

    List<T> findListByQuery(Query query) throws DaoException;

    List<T> findListByQuery(Query query, Pageable pageable) throws DaoException;

    List<T> findListBySql(String sql, Map<String, Object> param) throws DaoException;

    int insert(T entity) throws DaoException;

    int batchInsert(List<T> entity) throws DaoException;

    //更新实体所有属性
    int update(T entity) throws DaoException;

    //更新实体中指定的属性
    int update(T entity, List<String> propetyList) throws DaoException;

    int updateById(Serializable id, Update update) throws DaoException;

    int updateByIds(List<Serializable> ids, Update update) throws DaoException;

    int updateByCriteria(Criteria criteria, Update update) throws DaoException;

    int updateBySql(String sql, Map<String, Object> param) throws DaoException;

    int deleteById(Serializable id, String operator) throws DaoException;

    //2.0.0开始新增的查询接口，将Query拆分使用
    T findOne(List<String> fields, Criteria criteria) throws DaoException;

    T findOne(Criteria criteria) throws DaoException;

    List<T> findList(List<String> fields, Criteria criteria) throws DaoException;

    List<T> findList(List<String> fields, Criteria criteria, List<OrderBy> orderBys) throws DaoException;

    List<T> findList(List<String> fields, Criteria criteria, List<OrderBy> orderBys, Pageable pageable) throws DaoException;

    List<T> findList(Criteria criteria) throws DaoException;

    List<T> findList(Criteria criteria, List<OrderBy> orderBys) throws DaoException;

    List<T> findList(Criteria criteria, List<OrderBy> orderBys, Pageable pageable) throws DaoException;

    List<T> findAllList() throws DaoException;

    List<T> findAllList(List<String> fields) throws DaoException;

    List<T> findAllList(List<String> fields, List<OrderBy> orderBys) throws DaoException;

    List<T> findAllList(List<String> fields, List<OrderBy> orderBys, Pageable pageable) throws DaoException;

}
