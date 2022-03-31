package com.ziroom.framework.autoconfigure.jdbc;

import java.util.Properties;

/**
 * @Description
 * @Author lidm
 * @Date 2020/11/5
 */
public class ProviderManager {

    static Provider<Properties> propertiesProvider;

    static {

        propertiesProvider = new PropertiesProvider();
        propertiesProvider.initialize();

    }
    public static Provider<Properties> properties() {
        return propertiesProvider;
    }

}
