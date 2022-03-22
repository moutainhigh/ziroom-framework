package com.ziroom.framework.autoconfigure.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.ziroom.framework.apollo.Config;
import com.ziroom.framework.apollo.ConfigChangeListener;
import com.ziroom.framework.apollo.ConfigService;
import com.ziroom.framework.apollo.model.ConfigChange;
import com.ziroom.framework.apollo.model.ConfigChangeEvent;
import com.ziroom.framework.apollo.springx.ApolloUtils;
import com.ziroom.framework.foundation.Foundation;
import com.ziroom.gaea.gear.GearContext;
import com.ziroom.gaea.gear.constant.DBSensitiveKey;
import com.ziroom.gaea.gear.util.DBKeyUtil;
import com.ziroom.gaea.gear.util.JSONHelper;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.*;

/**
 * fetch db config</p>
 * build db
 *
 * @author LDM
 */
public class ConfigManagerApollo {
    private static Logger LOGGER = LoggerFactory.getLogger(ConfigManagerApollo.class);

    /**
     * appId
     */
    private static String appId;
    private static String DEFAULT_DB_GROUP = "default";
    private static String refreshDB_NS = "JSZX.JCPT.jdbc";
    private static String refreshDB_KEY = "refreshDB";
    private static boolean listenDBConfigChanged = false;

    private static Config config;
    @Getter
    private static Map<String, List<DruidConfig>> dbConfigs;
    @Getter
    @Setter
    private static boolean needRefreshDB = false;
    private static ConfigListener configListener;
    private static String SET_DB_GROUP = null;
    /**
     * app's jdbc config
     */
    private static Properties jdbcProperties = new Properties();

    /**
     * 判断是否幽静有配置变更监听器
     *
     * @return
     */
    public static boolean hasListener() {
        return configListener != null;
    }

    public static Properties buildDataSource(ConfigListener listener) {
        /**
         * notify db config changed
         */
        configListener = listener;
        return buildDataSource();

    }

    /**
     * mainly, init this app's id and fetch db config
     */
    public static Properties buildDataSource() {
        try {
            jdbcProperties = ApolloUtils.getProperties(GearContext.getDbPrivateNS());
        } catch (Exception e) {
            LOGGER.warn("获取自定义配置失败...");
        }
        removeDBSensitive();

        GearContext.setJdbcProperties(jdbcProperties);


        appId = Foundation.getProperty("app.id", "");
        if (StringUtils.isEmpty(appId)) {
            throw new RuntimeException("appId is null");
        }
        // fetch db config
        fetchDBConfig();
        return jdbcProperties;
    }

    private static void removeDBSensitive() {
        for (String key : DBSensitiveKey.DRUID_KEYWORDS) {
            jdbcProperties.remove(key);
        }
    }

    private static void fetchDBConfig() {

        // set config to connect pub ns
        config = ConfigService.getConfig(GearContext.getDbPublicNS());
        // 先添加配置监听器，再获取配置，否则，第一次获取配置会有延迟
        if (listenDBConfigChanged || configListener != null) {
            // set listener to dbConf
            Set<String> interestedKeys = new HashSet<>(2);
            interestedKeys.add(appId);
            // 监听数据源配置空间
            config.addChangeListener(new ConfigChangeListener() {
                                         @Override
                                         public void onChange(ConfigChangeEvent changeEvent) {
                                             ConfigChange configChange = changeEvent.getChange(appId);
                                             parse2DBConfig(configChange.getNewValue());
                                             if (configListener != null) {
                                                 configListener.resetDBConfig();
                                             }
                                         }
                                     },
                    interestedKeys);

            // 监听刷新db
            ConfigService.getConfig(refreshDB_NS).addChangeListener(new ConfigChangeListener() {
                @Override
                public void onChange(ConfigChangeEvent changeEvent) {
                    if (!changeEvent.isChanged(refreshDB_KEY)) {
                        LOGGER.debug("不需要刷新DB");
                        return;
                    }
                    ConfigChange change = changeEvent.getChange(refreshDB_KEY);

                    Date valDate = null;
                    try {
                        valDate = DateUtils.parseDate(change.getNewValue(), "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (valDate != null && DateUtils.isSameDay(valDate, new Date())) {
                        Config dbaJdbcConfig = ConfigService.getConfig(GearContext.getDbPublicNS());
                        String dbConfig = dbaJdbcConfig.getProperty(appId, null);
                        if (StringUtils.isEmpty(dbConfig)) {
                            LOGGER.error("获取数据库配置失败：{}", appId);
                            return;
                        }
                        parse2DBConfig(dbConfig);
                        if (configListener != null) {
                            configListener.resetDBConfig();
                        }
                    } else {
                        LOGGER.warn("刷新db通知不是今天，不进行刷新db");
                    }

                }
            });
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("pull db config from public namespace {} ", appId);
        }
        // add datasource
        String confJson = config.getProperty(appId, null);
        parse2DBConfig(confJson);

    }

    private static void parse2DBConfig(String confJson) {
        Map<String, List<DruidConfig>> newDBConfigs = initDBConfig(confJson);

        boolean hasException = false;

        try {
            // master or slave index
            Map<String, Integer> statusIndex = new HashMap<>(4);

            newDBConfigs.forEach((group, confList) -> {
                String masterDSName;
                Collection<String> slaveDSNames = new ArrayList<>(2);
                for (DBConfig conf : confList) {

                    conf.setGroup(group);
                    Integer index = statusIndex.get(conf.getStatus());
                    if (index == null) {
                        statusIndex.put(conf.getStatus(), 0);
                    } else {
                        statusIndex.put(conf.getStatus(), index++);
                    }

                    String dbKey = DBKeyUtil.DB_KEY_BY_GROUP(group, conf.getStatus(), statusIndex.get(conf.getStatus()));
                    conf.setDbKey(dbKey);

                }

            });
        } catch (Exception e) {
            hasException = true;
            LOGGER.error("new datasource init failed:{}", e);
            e.printStackTrace();
        }
        if (!hasException) {
            needRefreshDB = true;
            dbConfigs = newDBConfigs;
        }
        newDBConfigs = null;
    }

    private static Map<String, List<DruidConfig>> initDBConfig(String dbConf) {
        Map<String, List<DruidConfig>> result;
        switch (JSONHelper.getJSONType(dbConf)) {
            case JSON_TYPE_DICT:
                // more the one group
                result = parseDBGroupConfig(dbConf);
                break;
            case JSON_TYPE_ARRAY:
                // only a default group
                result = new HashMap<>(2);
                result.put(DEFAULT_DB_GROUP, parseToConfigArray(dbConf));

                break;
            case JSON_TYPE_ERROR:
                throw new RuntimeException("db config is empty: " + dbConf);
            default:
                throw new RuntimeException("db config error : " + dbConf);
        }
        return result;
    }

    private static Map<String, List<DruidConfig>> parseDBGroupConfig(String dbConf) {
        Map<String, List<DruidConfig>> result = new HashMap<>(2);

        // try to analyze db group
        JSONObject groupObj = JSON.parseObject(dbConf, Feature.OrderedField);
        groupObj.forEach((group, value) -> {
            if (StringUtils.isEmpty(SET_DB_GROUP)) {
                SET_DB_GROUP = group;
            }
            String jsonArr = value.toString();
            List<DruidConfig> dbConfigs = parseToConfigArray(jsonArr);
            fromatConfigKey(group, dbConfigs);

            result.put(group, dbConfigs);
        });
        return result;
    }

    @Deprecated
    private static Map<String, List<DruidConfig>> parseDbArrayConfig(String dbConf) {
        Map<String, List<DruidConfig>> result = new HashMap<>(2);
        List<DruidConfig> dbConfigs = JSON.parseArray(dbConf, DruidConfig.class);
        result.put(DEFAULT_DB_GROUP, dbConfigs);
        return result;
    }

    private static List<DruidConfig> parseToConfigArray(String array) {
        List<DruidConfig> druidConfigs = JSON.parseArray(array, DruidConfig.class);
        fromatConfigKey(DEFAULT_DB_GROUP, druidConfigs);
        return druidConfigs;
    }

    private static void fromatConfigKey(String groupName, List<DruidConfig> druidConfigs) {
        int index = 0;
        for (DruidConfig druidConfig : druidConfigs) {
            if (druidConfig.getStatus().equalsIgnoreCase("master")) {
                druidConfig.setDbKey(groupName + "_" + druidConfig.getStatus() + "_0");
            } else {
                druidConfig.setDbKey(groupName + "_" + druidConfig.getStatus() + "_" + index);
                index++;
            }

        }
    }

    public static String getDefaultDbGroup() {

        return SET_DB_GROUP;
    }
//    public static void main1(String[] args) {
//        String json = "{\n" +
//                "    \"gear\": [\n" +
//                "        {\n" +
//                "            \"status\": \"master\",\n" +
//                "            \"url\": \"jdbc:mysql://127.0.0.1:3306/gear_f_master?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&autoReconnect=true&failOverReadOnly=false\",\n" +
//                "            \"username\": \"root\",\n" +
//                "            \"password\": \"root\"\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}";
//        Map<String, List<DruidConfig>> stringListMap = initDBConfig(json);
//        System.out.println(stringListMap.size());
//    }
//    public static void main(String[] args) {
//        try {
//            Date date = DateUtils.parseDate("2019-06-26 15:15:15", "yyyy-MM-dd","yyyy-MM-dd HH:mm:ss");
//            System.out.println(date);
//            System.out.println(DateUtils.isSameDay(new Date(),date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }

}
