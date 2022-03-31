package com.ziroom.framework.autoconfigure.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;


/**
 * @author LDM
 */
public class DataSourceBuilder {
    private static final Logger log = LoggerFactory.getLogger(DataSourceBuilder.class);


    public static DataSource buildDataSource(DatabaseMeta druidConfig) {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.configFromPropety(ProviderManager.properties().get());
        dataSource.setUrl(druidConfig.url());
        dataSource.setUsername(druidConfig.getAppAccount().getUsername());
        dataSource.setPassword(druidConfig.getAppAccount().getPassword());

        // 取消连接失败重试
        dataSource.setBreakAfterAcquireFailure(false);
        dataSource.setConnectionErrorRetryAttempts(0);
        if (StringUtils.isEmpty(dataSource.getValidationQuery())) {
            dataSource.setValidationQuery("select 1");
        }
        try {
            // 测试连接,对多等10秒
            DruidPooledConnection connection = dataSource.getConnection(10 * 1000);
            if (connection == null) {
                if (!dataSource.isClosed()) {
                    dataSource.close();
                }
                dataSource = null;
            }
        } catch (SQLException e) {
            // 数据库连接不上，直接放弃重置数据库
            if (!dataSource.isClosed()) {
                dataSource.close();
            }
            dataSource = null;
            log.error(e.getMessage());

            e.printStackTrace();
        }


        return dataSource;
    }


    public static MasterSlaveDataSource buildMasterSlaveDataSource(Map<String, DataSource> groupDS, MasterSlaveRuleConfiguration masterSlaveRuleConfiguration) throws SQLException {
        return (MasterSlaveDataSource) MasterSlaveDataSourceFactory.createDataSource(groupDS, masterSlaveRuleConfiguration, GearContext.gearConfigProps);

    }

    /**
     * 构建分片数据源
     *
     * @param dataSourceMap
     * @param shardingRuleConfig
     * @return
     * @throws SQLException
     */
    public static ShardingDataSource buildShardingDataSource(Map<String, DataSource> dataSourceMap,
                                                             ShardingRuleConfiguration shardingRuleConfig) throws SQLException {
        return (ShardingDataSource) ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, GearContext.gearConfigProps);

    }

}
