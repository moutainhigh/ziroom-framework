package com.ziroom.framework.autoconfigure.jdbc;

import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;

import java.util.Properties;

/**
 * @author LDM
 */
public class GearContext {
    public static ShardingRuleConfiguration shardingRuleConfiguration;
    public static MasterSlaveRuleConfiguration masterSlaveRuleConfiguration;
    public static Properties gearConfigProps;

    // ===============================
    /**
     * public db config namespace's default value
     */
    private static String dbPublicNS = "dba-jdbc";
    @Deprecated
    private static String appId;
    /**
     * the private db config namespace default value
     */
    @Deprecated
    private static String dbPrivateNS = "jdbc";
    @Deprecated
    private static Properties jdbcProperties;


    public static Properties getJdbcProperties() {
        return jdbcProperties;
    }

    public static void setJdbcProperties(Properties jdbcProperties) {
        GearContext.jdbcProperties = jdbcProperties;
    }

    public static String getDbPublicNS() {
        return dbPublicNS;
    }

    public static void setDbPublicNS(String dbPublicNS) {
        GearContext.dbPublicNS = dbPublicNS;
    }

    public static String getAppId() {
        return appId;
    }

    public static void setAppId(String appId) {
        GearContext.appId = appId;
    }

    public static String getDbPrivateNS() {
        return dbPrivateNS;
    }

    public static void setDbPrivateNS(String dbPrivateNS) {
        GearContext.dbPrivateNS = dbPrivateNS;
    }
}
