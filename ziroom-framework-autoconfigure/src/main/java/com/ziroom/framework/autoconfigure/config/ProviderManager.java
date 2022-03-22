package com.ziroom.framework.autoconfigure.config;

import java.util.Properties;

/**
 * @Description
 * @Author lidm
 * @Date 2020/11/5
 */
public class ProviderManager {
    static Provider<String> appProvider;

    static Provider<ConfigManager> configProvider;

    static Provider<Properties> propertiesProvider;

    static {
        appProvider = new ApplicationProvider();
        appProvider.initialize();

        configProvider = new ConfigProvider();
        configProvider.initialize();

        propertiesProvider = new PropertiesProvider();
        propertiesProvider.initialize();

    }

    public static String getAppId() {
        return appProvider.get();
    }

    public static Provider<ConfigManager> configManager() {
        return configProvider;
    }

    public static Provider<Properties> properties() {
        return propertiesProvider;
    }

}
