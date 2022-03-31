package com.ziroom.framework.autoconfigure.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.MasterSlaveConnection;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.connection.ShardingConnection;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.MasterSlaveDataSource;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author LDM
 */

public class GearDatasource implements DataSource {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(GearDatasource.class);

    @Autowired
    private ApplicationContext applicationContext;
    private PrintWriter logWriter = new PrintWriter(System.out);

    private String defaultDB;
    private static final String REFRESH_DNS = "refresh_dns";
    /**
     * dbId,raw datasource
     */
    private static Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>(4);
    /**
     * 数据源keyMap
     * <groupKey, Collection<dbId>>
     */
    private static Map<String, List<String>> dataSourceGroupMap = new HashMap<>(4);
    /**
     * 分组主库的关系
     * <groupKey,masterId>
     */
    private static Map<String, String> dataSourceMasterMap = new HashMap<>(2);
    /**
     * <groupKey,RuleConfig>
     */
    private static Map<String, MasterSlaveRuleConfiguration> masterSlaveRuleConfigurationMap = new HashMap<>(2);
    /**
     * <dbId,datasource>
     */
    private static Map<String, MasterSlaveDataSource> masterSlaveDataSourceMap = new ConcurrentHashMap<>(2);

    private static Map<String, ShardingDataSource> shardingDataSourceMap = new ConcurrentHashMap<>(2);
    /**
     * <shardingName,Set<groupKey>>
     */
    private static Map<String, List<String>> shardingGroupAggregation = new HashMap<>(2);


    public GearDatasource() {

    }

    @PostConstruct
    private void initDbConf() {

        try {
//            ImmutableMultimap<String, DatabaseMeta> dbConfigs = configManager.getDatabase();
            //TODO
            ImmutableMultimap.Builder<String, DatabaseMeta> dbGroup = ImmutableMultimap.builder();
            ImmutableMultimap<String, DatabaseMeta> dbConfigs = dbGroup.build();
            initDataSource(dbConfigs);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        initDefaultDB();
    }

    public MasterSlaveDataSource getDatasource(String groupKey) {
        return masterSlaveDataSourceMap.get(groupKey);
    }


    /**
     * 需要满足，动态刷新db的要求，需要对新数据源进行先建立，后替换
     *
     * @param dbConfigs
     * @throws SQLException
     */
    private void initDataSource(ImmutableMultimap<String/*db group*/, DatabaseMeta> dbConfigs) throws SQLException {

        // 和成员变量 dataSourceGroupMap 对应，先进行新的副本构建成功后，再对旧的副本进行销毁
        // <groupKey, Collection<dbId>>
        Map<String, List<String>> newDataSourceGroupMap = new HashMap<>(4);
        //和成员变量 dataSourceMasterMap 对应，先进行新的副本构建成功后，再对旧的副本进行销毁
        Map<String, String> newDataSourceMasterMap = new HashMap<>(2);
        //和成员变量 dataSourceMap 对应，先进行新的副本构建成功后，再对旧的副本进行销毁
        Map<String, DataSource> newDataSourceMap = new ConcurrentHashMap<>(4);

        // 构建原子数据源
        buildAtomDatabase(dbConfigs,
                newDataSourceGroupMap,
                newDataSourceMasterMap,
                newDataSourceMap);

        newDataSourceMap = convertDBInstanceToBean(newDataSourceMap);
        // 新数据源构建成功
        //--------------------
        // 构建主从规则；先构建新的副本
        // <group_key,MasterSlaveRuleConfigure>
        Map<String, MasterSlaveRuleConfiguration> newMasterSlaveRuleConfigurationMap = initMasterSlaveRuleConfig(newDataSourceGroupMap, newDataSourceMasterMap);

        //================================
        // 判断数据库是主从结构还是分库结构   ||
        // ===============================

        // 数据库全部构建为主从结构
        if (isNotSharding()) {
            Map<String, MasterSlaveDataSource> newMasterSlaveDataSourceMap =
                    initMasterSlaveDataSource(newDataSourceMasterMap,
                            newDataSourceGroupMap,
                            newDataSourceMap,
                            newMasterSlaveRuleConfigurationMap);
            // 数据源初始化完成，进行属性的迁移
            dataSourceGroupMap = newDataSourceGroupMap;

            Map<String, DataSource> closeDataSourceMap = dataSourceMap;
            dataSourceMap = newDataSourceMap;
            dataSourceMasterMap = newDataSourceMasterMap;
            masterSlaveDataSourceMap = newMasterSlaveDataSourceMap;
            masterSlaveRuleConfigurationMap = newMasterSlaveRuleConfigurationMap;
            // 关闭源数据源
            closeDataSource(closeDataSourceMap);
        } else {
            // 构建sharding 分组
            // <sharding_name,Set<group_key>>
            HashMap<String, List<String>> newShardingGroupAggregation = initSharding(newDataSourceGroupMap);
            HashMap<String, ShardingDataSource> newShardingDataSourceMap = initShardingDataSource(newShardingGroupAggregation,
                    newDataSourceMasterMap,
                    newDataSourceGroupMap,
                    newDataSourceMap);

            // 数据源初始化完成，进行属性的迁移
            dataSourceGroupMap = newDataSourceGroupMap;
            Map<String, DataSource> closeDataSourceMap = dataSourceMap;
            dataSourceMap = newDataSourceMap;
            dataSourceMasterMap = newDataSourceMasterMap;
            shardingDataSourceMap = newShardingDataSourceMap;
            masterSlaveRuleConfigurationMap = newMasterSlaveRuleConfigurationMap;
            // 关闭旧数据源
            closeDataSource(closeDataSourceMap);
        }
    }

    /**
     * 构建原子数据源
     *
     * @param dbConfigs
     * @param newDataSourceGroupMap
     * @param newDataSourceMasterMap
     * @param newDataSourceMap
     */
    private static void buildAtomDatabase(ImmutableMultimap<String/*db group*/, DatabaseMeta> dbConfigs,
                                          Map<String, List<String>> newDataSourceGroupMap,
                                          Map<String, String> newDataSourceMasterMap,
                                          Map<String, DataSource> newDataSourceMap) {
        for (String groupKey : dbConfigs.keySet()) {

            List<String> dbKeys = newDataSourceGroupMap.get(groupKey);
            if (dbKeys == null) {
                dbKeys = new ArrayList<>(2);
                newDataSourceGroupMap.put(groupKey, dbKeys);
            }
            for (DatabaseMeta conf : dbConfigs.get(groupKey)) {
                if ("master".equalsIgnoreCase(conf.getRole())) {
                    newDataSourceMasterMap.put(groupKey, conf.getBeanName());
                } else {
                    dbKeys.add(conf.getBeanName());
                }

                DataSource dataSource = DataSourceBuilder.buildDataSource(conf);
                if (dataSource == null) {
                    newDataSourceMap.clear();
                    // 数据库初始化失败，停止数据源构建
                    logger.error("数据库初始化失败，停止数据源构建；保留原数据源");
                    break;
                }
                // global datasource
                newDataSourceMap.put(conf.getBeanName(), dataSource);
            }
            if (newDataSourceMap.size() == 0) {
                throw new RuntimeException("no database");
            }
            if (dbKeys.size() == 0) {
                dbKeys.add(newDataSourceMasterMap.get(groupKey));
            } else if (newDataSourceMasterMap.get(groupKey) == null) {
                newDataSourceMasterMap.put(groupKey, dbKeys.get(0));
            }
        }
    }

    private Map<String, DataSource> convertDBInstanceToBean(Map<String, DataSource> newDataSourceMap) {
        Map<String, DataSource> dbBeanMap = Maps.newHashMap();
        for (Map.Entry<String, DataSource> entry :
                newDataSourceMap.entrySet()) {
            GearDefinition.registerAndRemove(entry.getKey(), entry.getValue(), "init", "close");

            dbBeanMap.put(entry.getKey(), (DataSource) applicationContext.getBean(entry.getKey()));
        }
        newDataSourceMap = dbBeanMap;
        return newDataSourceMap;
    }

    private static boolean isNotSharding() {
        return GearContext.shardingRuleConfiguration == null;
//                || GearContext.shardingRuleConfiguration.getDefaultKeyGeneratorConfig() == null
//                || GearContext.shardingRuleConfiguration.getTableRuleConfigs().size() == 0;
    }

    private static void closeDataSource(Map<String, DataSource> closeDataSourceMap) {
        logger.info("新数据源已经完成初始化，开始关闭旧数据源");
        for (DataSource dataSource : closeDataSourceMap.values()) {
            if (dataSource != null) {
                if (dataSource instanceof DruidDataSource) {
                    DruidDataSource druidDataSource = (DruidDataSource) dataSource;
                    try {
                        druidDataSource.close();
                    } catch (Exception e) {
                        logger.error("关闭数据源失败,{}", e);
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    private static Map<String, MasterSlaveRuleConfiguration> initMasterSlaveRuleConfig(Map<String, List<String>> currDataSourceGroupMap, Map<String, String> newDataSourceMasterMap) {
        Map<String, MasterSlaveRuleConfiguration> newMasterSlaveRuleConfigurationMap = new HashMap<>(2);
        for (String groupKey : currDataSourceGroupMap.keySet()) {
            MasterSlaveRuleConfiguration masterSlaveRuleConfiguration =
                    RuleConfigureBuilder.buildMasterSlaveRuleConfiguration(
                            groupKey,
                            newDataSourceMasterMap.get(groupKey),
                            currDataSourceGroupMap.get(groupKey)
                    );
            newMasterSlaveRuleConfigurationMap.put(groupKey, masterSlaveRuleConfiguration);
        }
        return newMasterSlaveRuleConfigurationMap;
    }


    private static Map<String, MasterSlaveDataSource> initMasterSlaveDataSource(Map<String, String> currDataSourceMasterMap,
                                                                                Map<String, List<String>> currDataSourceGroupMap,
                                                                                Map<String, DataSource> currDataSourceMap,
                                                                                Map<String, MasterSlaveRuleConfiguration> currMasterSlaveRuleConfigurationMap)
            throws SQLException {
        Map<String, MasterSlaveDataSource> newMasterSlaveDataSourceMap = new ConcurrentHashMap<>(2);
        for (String groupKey : currDataSourceGroupMap.keySet()) {
            MasterSlaveDataSource masterSlaveDataSource =
                    DataSourceBuilder.buildMasterSlaveDataSource(
                            filterDatasource(groupKey,
                                    currDataSourceMasterMap,
                                    currDataSourceMap,
                                    currDataSourceGroupMap
                            ),
                            currMasterSlaveRuleConfigurationMap.get(groupKey));
            newMasterSlaveDataSourceMap.put(groupKey, masterSlaveDataSource);
        }
        return newMasterSlaveDataSourceMap;
    }


    private static HashMap<String, ShardingDataSource> initShardingDataSource(Map<String, List<String>> currShardingGroupAggregation,
                                                                          Map<String, String> currDataSourceMasterMap,
                                                                          Map<String, List<String>> currDataSourceGroupMap,
                                                                          Map<String, DataSource> currDataSourceMap) throws SQLException {

        HashMap<String, ShardingDataSource> newShardingDataSourceMap = new HashMap<>(2);
        // build sharding data source
        for (String shardingName : currShardingGroupAggregation.keySet()) {
            Collection<MasterSlaveRuleConfiguration> masterSlaveRuleConfigurations = new ArrayList<>(2);
            Map<String, DataSource> shardingDatasource = new HashMap<>(2);
            for (String groupKey : currShardingGroupAggregation.get(shardingName)) {
                masterSlaveRuleConfigurations.add(
                        RuleConfigureBuilder.buildMasterSlaveRuleConfiguration(
                                groupKey,
                                currDataSourceMasterMap.get(groupKey),
                                currDataSourceGroupMap.get(groupKey)
                        )
                );
                shardingDatasource.putAll(filterDatasource(groupKey,
                        currDataSourceMasterMap,
                        currDataSourceMap,
                        currDataSourceGroupMap));
            }

            // 对分片规则进行深度拷贝，分片规则不允许动态变更，
            ShardingRuleConfiguration newShardingRuleConfiguration = deepCloneShardingRuleConfiguration();
            newShardingRuleConfiguration.setMasterSlaveRuleConfigs(masterSlaveRuleConfigurations);
            ShardingDataSource shardingDataSource = DataSourceBuilder.buildShardingDataSource(shardingDatasource, newShardingRuleConfiguration);
            newShardingDataSourceMap.put(shardingName, shardingDataSource);
        }

        return newShardingDataSourceMap;
    }

    private static ShardingRuleConfiguration deepCloneShardingRuleConfiguration() {
        ShardingRuleConfiguration shardingRule = new ShardingRuleConfiguration();
        RuntimeSchema<ShardingRuleConfiguration> schema = RuntimeSchema.createFrom(ShardingRuleConfiguration.class);
        byte[] bytes = ProtostuffIOUtil.toByteArray(GearContext.shardingRuleConfiguration, schema, LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
        ProtostuffIOUtil.mergeFrom(bytes, shardingRule, schema);
        return shardingRule;
    }

    private static Map<String, DataSource> filterDatasource(String groupKey,
                                                            Map<String, String> currDataSourceMasterMap,
                                                            Map<String, DataSource> currDataSourceMap,
                                                            Map<String, List<String>> currDataSourceGroupMap) {
        Map<String, DataSource> result = new HashMap<>(2);
        String masterKey = currDataSourceMasterMap.get(groupKey);
        result.put(masterKey, currDataSourceMap.get(masterKey));
        List<String> dbKeys = currDataSourceGroupMap.get(groupKey);
        dbKeys.forEach(key -> {
            if (currDataSourceMap.get(key) != null) {
                result.put(key, currDataSourceMap.get(key));
            }
        });
        return result;
    }


    /**
     * 进行数据源分片聚合
     */

    private static HashMap<String, List<String>> initSharding(Map<String, List<String>> newDataSourceGroupMap) {
        HashMap<String, List<String>> currShardingGroupAggregation = new HashMap<>(2);
        for (String groupKey : newDataSourceGroupMap.keySet()) {
            aggregateSharding(groupKey, currShardingGroupAggregation);
        }
        return currShardingGroupAggregation;
    }


    /**
     * 对数据源根据分库分表逻辑进行聚合
     *
     * @param groupKey
     */
    private static void aggregateSharding(String groupKey,
                                          Map<String, List<String>> currShardingGroupAggregation) {
        String shardingName = getShardingGroupName(groupKey);
        List<String> groupNames = currShardingGroupAggregation.get(shardingName);
        if (groupNames == null) {
            groupNames = new ArrayList<>(2);
            currShardingGroupAggregation.put(shardingName, groupNames);
        }
        groupNames.add(groupKey);
    }


    private static String getShardingGroupName(String groupName) {
        return groupName.replaceFirst("_\\d+$", "");
    }

    private ShardingDataSource determineShardingDataSource() {
        String dbId = DataBaseVisitedManager.getVisitedDB();
        if (StringUtils.isEmpty(dbId)) {
            dbId = defaultDB;
        }
        if (StringUtils.isEmpty(dbId)) {
            throw new RuntimeException("未设置需要访问的dbId");
        }
        return shardingDataSourceMap.get(dbId);
    }



    private MasterSlaveDataSource determineMasterSlaveDataSource() {
        String dbId = DataBaseVisitedManager.getVisitedDB();
        if (StringUtils.isEmpty(dbId)) {
            dbId = defaultDB;
        }
        if (StringUtils.isEmpty(dbId)) {
            throw new RuntimeException("未设置需要访问的dbId");
        }

        return (MasterSlaveDataSource) masterSlaveDataSourceMap.get(dbId);
    }

    /**
     * init default db after finished all datasource.
     */
    private void initDefaultDB() {
        if (StringUtils.isEmpty(defaultDB)) {
            if (isNotSharding()) {
                if (masterSlaveDataSourceMap.size() > 0) {
                    defaultDB = masterSlaveDataSourceMap.keySet().iterator().next();
                }
            } else {

                if (shardingDataSourceMap.size() > 0) {
                    //FIXME 需要允许用户配置默认数据库组
                    defaultDB = shardingDataSourceMap.keySet().iterator().next();
                }
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (isNotSharding()) {
            MasterSlaveDataSource masterSlaveDataSource = determineMasterSlaveDataSource();
            return new MasterSlaveConnection(masterSlaveDataSource.getDataSourceMap(), masterSlaveDataSource.getRuntimeContext());
        }
        ShardingDataSource shardingDataSource = determineShardingDataSource();
        return new ShardingConnection(shardingDataSource.getDataSourceMap(), shardingDataSource.getRuntimeContext(), TransactionType.LOCAL);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return getConnection();
    }


    @Override
    public final <T> T unwrap(final Class<T> iface) throws SQLException {
        if (isWrapperFor(iface)) {
            return (T) this;
        }
        throw new SQLException(String.format("[%s] cannot be unwrapped as [%s]", getClass().getName(), iface.getName()));
    }

    @Override
    public final boolean isWrapperFor(final Class<?> iface) {
        return iface.isInstance(this);
    }


    @Override
    public final PrintWriter getLogWriter() {
        return logWriter;
    }

    @Override
    public final void setLogWriter(final PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public final Logger getParentLogger() {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    public boolean isValid(){
        // FIXME not supported
        return true;
    }

    /**
     * unsupported
     *
     * @param seconds
     * @throws SQLException
     */
    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("unsupported setLoginTimeout(int seconds)");
    }

    /**
     * unsupported
     *
     * @return
     * @throws SQLException
     */
    @Override
    public int getLoginTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("unsupported getLoginTimeout()");
    }

    public static Map<String, List<String>> getGroupBeanList() {
        return dataSourceGroupMap;
    }
}
