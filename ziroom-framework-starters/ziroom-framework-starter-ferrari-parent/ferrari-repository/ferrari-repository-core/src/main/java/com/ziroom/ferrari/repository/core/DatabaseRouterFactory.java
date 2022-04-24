package com.ziroom.ferrari.repository.core;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:46
 * @Version 1.0
 */
public interface DatabaseRouterFactory {

    DatabaseRouter getDatabaseRouter(DaoSettings daoSettings);

    void setDatabaseRouter(DaoSettings daoSettings);
}
