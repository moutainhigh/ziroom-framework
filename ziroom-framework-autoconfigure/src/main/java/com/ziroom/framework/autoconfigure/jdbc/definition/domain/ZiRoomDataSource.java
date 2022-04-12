package com.ziroom.framework.autoconfigure.jdbc.definition.domain;

import org.springframework.core.style.ToStringCreator;

import java.util.Map;

/**
 * /app/conf/数据库链接配置文件
 */
public class ZiRoomDataSource {


    private String appId;

    private String prefix;

    Config ConfigObject;

    Map<String,String> properties;

    public String getAppId() {
        return appId;
    }

    public String getPrefix() {
        return prefix;
    }

    public Config getConfig() {
        return ConfigObject;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setConfig(Config configObject) {
        this.ConfigObject = configObject;
    }


    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public static class Config {

        private String classify;

        private String primary;

        public String getClassify() {
            return classify;
        }

        public void setClassify(String classify) {
            this.classify = classify;
        }

        public String getPrimary() {
            return primary;
        }

        public void setPrimary(String primary) {
            this.primary = primary;
        }
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).toString();
    }
}
