package com.ziroom.framework.autoconfigure.common;


import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
@Configuration
public class ApplicationProvider implements ApplicationContextInitializer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationProvider.class);

    public static final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";

    Properties appProperties = new Properties();

    protected String appId;

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        try {
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
            if (in == null) {
                in = ApplicationProvider.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
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
        if (CommonMixUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info(String.format("App ID is set to {} by app.id property from {}", appId, APP_PROPERTIES_CLASSPATH));
            return;
        }
        // 1. Get app.id from System Property
        appId = System.getProperty("app.id");
        if (CommonMixUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info(String.format("App ID is set to {} by app.id property from System Property", appId));
            return;
        }

        // 2. Try to get app id from app.properties.
        appId = appProperties.getProperty("app.id");
        if (CommonMixUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info(String.format("App ID is set to {} by app.id property from {}", appId, APP_PROPERTIES_CLASSPATH));
            return;
        }

        // 3. Try to get app id from environment
        appId = System.getenv("APP_ID");
        if (CommonMixUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info("App ID is set to {} by APP_ID variable from system environment");
            return;
        }

        // 4. Try to get app  id from omega environment
        appId = System.getenv("MODULE");
        if (CommonMixUtils.isNotBlank(appId)) {
            appId = appId.trim();
            log.info("App ID is set to {} by MODULE variable from system environment");
            return;
        }

        appId = null;
        log.warn(String.format("app.id is not available from System Property and {}. It is set to null", APP_PROPERTIES_CLASSPATH));
    }

    public String getAppId() {
        return appId;
    }
}
