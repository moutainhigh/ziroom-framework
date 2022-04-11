package com.ziroom.framework.autoconfigure.jdbc.definition.domain;

import org.springframework.core.style.ToStringCreator;

import java.util.Properties;

/**
 * /app/conf/数据库链接配置文件
 */
public class ZiRoomDataSource {

        private String appId;

        private String prefix;

        Config ConfigObject;

        Properties properties;

        public String getAppId() {
            return appId;
        }

        public String getPrefix() {
            return prefix;
        }

        public Config getConfig() {
            return ConfigObject;
        }

        public Properties getProperties() {
            return properties;
        }

        // Setter Methods

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void setConfig(Config configObject) {
            this.ConfigObject = configObject;
        }

        public void setProperties(Properties properties) {
            this.properties = properties;
        }
    public static class Config {

        private String classify;


        // Getter Methods

        public String getClassify() {
            return classify;
        }

        // Setter Methods

        public void setClassify(String classify) {
            this.classify = classify;
        }
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).toString();
    }
}
