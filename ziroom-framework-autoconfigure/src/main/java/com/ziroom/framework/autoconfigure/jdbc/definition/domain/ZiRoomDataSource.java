package com.ziroom.framework.autoconfigure.jdbc.definition.domain;

/**
 * /app/conf/数据库链接配置文件
 */
public class ZiRoomDataSource {

        private String appId;

        private String prefix;

        Config ConfigObject;

        Properties PropertiesObject;

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
            return PropertiesObject;
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

        public void setProperties(Properties propertiesObject) {
            this.PropertiesObject = propertiesObject;
        }

    public static class Properties {

        private String name;

        private String url;

        private String username;

        private String password;


        // Getter Methods

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        // Setter Methods

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }
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

//    @Override
//    public String toString() {
////        return "ZiRoomDataSource [appId=" + appId + ", prefix=" + prefix + ", Config =" + version + ", opaque=" + opaque + ", flag(B)="
////                + Integer.toBinaryString(flag) + ", remark=" + remark + ", extFields=" + extFields + ", serializeTypeCurrentRPC="
////                + serializeTypeCurrentRPC + "]";
//    }
}
