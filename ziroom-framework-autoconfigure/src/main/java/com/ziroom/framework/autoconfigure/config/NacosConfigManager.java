package com.ziroom.framework.autoconfigure.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ziroom.gaea.gear.domain.AppAccount;
import com.ziroom.gaea.gear.domain.DatabaseMeta;
import com.ziroom.gaea.gear.util.GearConfigUtil;
import com.ziroom.gaea.gear.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;


/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
@Slf4j
public class NacosConfigManager implements ConfigManager {
    private static final long NACOS_REQ_TIMEOUT_MS = 5000;
    private static final String NACOS_DB_CONFIG_GROUP = "db";
    private static final String NACOS_APP_DB_GROUP = "app-db";

    private static final String OMEGA_NACOS_HOST = "GAMMA_NACOS_SERVER_HOST";
    private static final String DEFAULT_NACOS_HOST = "registry.gamma.t.ziroom.com";
    private static final String NAMESPACE_PREFIX = "gear-";
    private static final String DEFAULT_ENV = "daily";

    Properties nacosProperties = new Properties();
    ConfigService configService;
    ConfigListener configListener;


    @Override
    public void initialize() throws NacosException {
        String serverHost = System.getenv(OMEGA_NACOS_HOST);
        if (StringUtils.isEmpty(serverHost)) {
            log.info("read nacos addr failed by  {} from environment，use default nacos host:{}", OMEGA_NACOS_HOST, DEFAULT_NACOS_HOST);
            serverHost = DEFAULT_NACOS_HOST;
        }

        nacosProperties.setProperty("serverAddr", serverHost);

        // 1. 从环境变量加载
        String env = System.getenv("APPLICATION_ENV");
        if (StringUtils.isEmpty(env)) {
            // 2. 从 启动命令 参数读取
            env=System.getProperty("APPLICATION_ENV");
        }
        if (StringUtils.isEmpty(env)) {
            // 3. 从 ENV 参数读取
            env=System.getProperty("ENV");
        }
        if (StringUtils.isEmpty(env)) {
            log.info("read env from environment failed. use default environment:{}", DEFAULT_ENV);
            env = DEFAULT_ENV;
        }
        nacosProperties.setProperty("namespace", NAMESPACE_PREFIX + env);
        configService = NacosFactory.createConfigService(nacosProperties);


    }


    private String getAccount(String appId) throws NacosException {
        String config = configService.getConfigAndSignListener(appId, NACOS_APP_DB_GROUP, NACOS_REQ_TIMEOUT_MS, getListener());
        return config;
    }

    @Override
    public List<AppAccount> getAccount() throws NacosException {
        List<AppAccount> list = Lists.newArrayList();
        String account = getAccount(ProviderManager.getAppId());
        if (StringUtils.isEmpty(account)) {
            log.warn("从nacos读取 {} 的关联数据库账号失败", ProviderManager.getAppId());
            return list;
        }


        JSONObject dbMap = JSON.parseObject(account);
        dbMap.keySet().forEach(dbId -> {
            AppAccount appAccount = dbMap.getObject(dbId, AppAccount.class);
            appAccount.setDbId(dbId);
            list.add(appAccount);
        });
        return list;
    }

    private String getDatabase(String dbId) throws NacosException {
        String config = configService.getConfigAndSignListener(dbId, NACOS_DB_CONFIG_GROUP, NACOS_REQ_TIMEOUT_MS, getListener());
        if (StringUtils.isEmpty(config)) {
            throw new RuntimeException("read db[" + dbId + "] config failed");
        }
        return config;
    }

    private Listener getListener() {
        return new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String s) {
                configListener.resetDBConfig();
            }
        };
    }

    @Override
    public ImmutableMultimap<String, DatabaseMeta> getDatabase() throws NacosException {
        ImmutableMultimap.Builder<String, DatabaseMeta> dbGroup = ImmutableMultimap.builder();
        List<AppAccount> accountList = getAccount();
        accountList.forEach(account -> {
            try {
                String group = account.getDbId();
                List<DatabaseMeta> databaseMetas = JSON.parseArray(getDatabase(group), DatabaseMeta.class);

                Map<String, Integer> dbIndex = Maps.newHashMap();
                dbIndex.put("master", 0);
                dbIndex.put("slave", 0);
                databaseMetas.forEach(db -> {
                    db.setAppAccount(account);
                    db.setGroup(group);
                    db.setBeanName(GearConfigUtil.DB_KEY_BY_GROUP(group, db.getRole(), dbIndex.get(db.getRole())));
                });
                dbGroup.putAll(group, databaseMetas);
            } catch (NacosException e) {
                e.printStackTrace();
            }
        });
        return dbGroup.build();
    }

    @Override
    public void listener(ConfigListener configListener) {
        this.configListener = configListener;
    }

}
