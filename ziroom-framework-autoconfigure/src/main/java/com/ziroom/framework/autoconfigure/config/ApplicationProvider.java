package com.ziroom.framework.autoconfigure.config;

import com.ziroom.framework.foundation.internals.io.BOMInputStream;
import com.ziroom.framework.foundation.internals.provider.DefaultApplicationProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
@Slf4j
public class ApplicationProvider implements Provider<String> {
    public static final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";
    Properties appProperties = new Properties();
    String appId;

    @Override
    public void initialize() {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
            if (in == null) {
                in = DefaultApplicationProvider.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
            }

            initialize(in);
        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }


    public void initialize(InputStream in) {
        try {
            if (in != null) {
                try {
                    appProperties.load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
                } finally {
                    in.close();
                }
            }

            initAppId();
        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }

    private void initAppId() {
        // 0. Try to get app id from app.properties.
        appId = appProperties.getProperty("omega.name");
        if (StringUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info("App ID is set to {} by app.id property from {}", appId, APP_PROPERTIES_CLASSPATH);
            return;
        }
        // 1. Get app.id from System Property
        appId = System.getProperty("app.id");
        if (StringUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info("App ID is set to {} by app.id property from System Property", appId);
            return;
        }

        // 2. Try to get app id from app.properties.
        appId = appProperties.getProperty("app.id");
        if (StringUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info("App ID is set to {} by app.id property from {}", appId, APP_PROPERTIES_CLASSPATH);
            return;
        }

        // 3. Try to get app id from environment
        appId = System.getenv("APP_ID");
        if (StringUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info("App ID is set to {} by APP_ID variable from system environment");
            return;
        }

        // 4. Try to get app  id from omega environment
        appId = System.getenv("MODULE");
        if (StringUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info("App ID is set to {} by MODULE variable from system environment");
            return;
        }

        appId = null;
        log.warn("app.id is not available from System Property and {}. It is set to null", APP_PROPERTIES_CLASSPATH);
    }

    @Override
    public String get() {
        // 1. 从配置文件获取
        // 2. 从输入参数获取
        // 3. 从环境获取

        return appId;
    }
}
