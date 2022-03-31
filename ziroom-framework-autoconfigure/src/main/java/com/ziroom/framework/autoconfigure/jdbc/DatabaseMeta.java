package com.ziroom.framework.autoconfigure.jdbc;

import lombok.Data;

/**
 * @Description
 * @Author lidm
 * @Date 2020/11/5
 */
@Data
public class DatabaseMeta {
    // 生成Spring bean name 替换原配置中的 db key
    private String beanName;
    // 数据库的分组名
    private String group;
    private String type;
    private String role;
    private String ip;
    private int port;
    private String params;
    private String name;
    private AppAccount appAccount;


    public String url(){
        return GearConfigUtil.SQL_URL(type,ip,port,name,params);
    }

}
