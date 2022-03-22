package com.ziroom.framework.autoconfigure.config;

import com.ziroom.framework.apollo.ConfigChangeListener;
import com.ziroom.framework.apollo.ConfigService;
import com.ziroom.framework.apollo.model.ConfigChangeEvent;
import com.ziroom.framework.apollo.springx.ApolloUtils;
import com.ziroom.framework.foundation.internals.provider.DefaultApplicationProvider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 只支持配置DB属性
 * @Description
 * @Author lidm
 * @Date 2020/11/5
 */
@Slf4j
public class PropertiesProvider implements Provider<Properties> {

    private final static String JDBC_PROPERTIES_PATH = "jdbc.properties";
    private final static String APOLLO_JDBC_PROPERTIES = "jdbc";

    Properties properties = new Properties();

    @Override
    public void initialize() {
        //1. 判断是否引入apollo
        if (isApollo()) {
            loadFromApollo();
        } else {
            //2. 从本地文件加载
            try {
                loadFromLocal();
            } catch (IOException e) {
                log.warn("从本地加载数据源配置失败");
            }
        }


    }

    private boolean isApollo() {
        try {
            Class.forName("com.ziroom.framework.apollo.springx.ApolloUtils");
            return true;
        } catch (ClassNotFoundException e) {
            log.warn("不支持从Apollo读取数据源配置");
        }
        return true;
    }

    private void loadFromApollo() {
        properties = ApolloUtils.getProperties(APOLLO_JDBC_PROPERTIES);
        ConfigService.getConfig(APOLLO_JDBC_PROPERTIES).addChangeListener(new ConfigChangeListener() {
            @Override
            public void onChange(ConfigChangeEvent changeEvent) {
                properties = ApolloUtils.getProperties(APOLLO_JDBC_PROPERTIES);
            }
        });
    }

    private void loadFromLocal() throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(JDBC_PROPERTIES_PATH);
        if (in == null) {
            in = DefaultApplicationProvider.class.getResourceAsStream(JDBC_PROPERTIES_PATH);
        }
        properties.load(in);

    }

    @Override
    public Properties get() {
        return properties;
    }
}
