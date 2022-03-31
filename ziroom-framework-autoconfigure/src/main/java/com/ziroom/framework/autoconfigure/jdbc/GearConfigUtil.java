package com.ziroom.framework.autoconfigure.jdbc;

import org.apache.commons.lang3.StringUtils;

/**
 * @author LDM
 */
public class GearConfigUtil {
    /**
     * @param group  <=> dbgroup
     * @param status
     * @param index
     * @return
     */
    public static String DB_KEY_BY_GROUP(String group, String status, int index) {
        String dbKey = String.format("%s_%s_%s", group.toLowerCase(), status, index);
        System.out.println("generate db key: " + dbKey);
        return dbKey;
    }

    public static String SQL_URL(String type, String ip, int port,String dbName, String params) {
        if (StringUtils.isEmpty(params)) {
            // null 会在末尾追加 "null"
            params = "";
        } else if (!params
                .startsWith("?")) {
            params = "?" + params;
        }
        return String.format("jdbc:%s://%s:%s/%s%s", type, ip, port,dbName, params);
    }
}
