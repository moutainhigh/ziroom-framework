package com.ziroom.ferrari.repository.core.jdbc;

import com.google.common.collect.Maps;
import com.ziroom.ferrari.repository.core.DaoSettings;
import com.ziroom.ferrari.repository.core.DatabaseRouter;
import com.ziroom.ferrari.repository.core.DatabaseRouterFactory;
import com.ziroom.ferrari.repository.core.exception.DaoException;

import java.util.HashMap;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:29
 * @Version 1.0
 */
public class JdbcTemplateRouterFactory implements DatabaseRouterFactory {
    static final JdbcTemplateRouterFactory INSTANCE = new JdbcTemplateRouterFactory();
    private HashMap<JdbcSettings, JdbcTemplateRouter> jdbcTemplateRouterMap = Maps.newHashMap();

    /**
     * JdbcTemplateRouter的获取发生在项目运行中
     *
     * @param daoSettings - daoSettings
     */
    @Override
    public DatabaseRouter getDatabaseRouter(DaoSettings daoSettings) {
        return jdbcTemplateRouterMap.get(daoSettings);
    }

    /**
     * JdbcTemplateRouter的创建工作发生在项目启动过程
     *
     * @param daoSettings - daoSettings
     */
    @Override
    public synchronized void setDatabaseRouter(DaoSettings daoSettings) {
        if (getDatabaseRouter(daoSettings) != null) {
            return;
        }
        JdbcSettings jdbcSettings = (JdbcSettings) daoSettings;
        try {
            jdbcTemplateRouterMap.put(jdbcSettings, new JdbcTemplateRouter(jdbcSettings));
        } catch (RuntimeException e) {
            throw new DaoException("无法生产JdbcTemplateRouter[" + jdbcSettings + "]", e);
        }
    }
}
