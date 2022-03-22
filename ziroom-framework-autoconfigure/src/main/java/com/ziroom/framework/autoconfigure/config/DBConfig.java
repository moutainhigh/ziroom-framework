package com.ziroom.framework.autoconfigure.config;


import lombok.Getter;
import lombok.Setter;

/**
 * @author LDM
 */
@Setter
@Getter
@Deprecated
public class DBConfig {
     private String dbKey;
     private String group;
     private String status;
     private String dbType;
     private String url;
     private String username;
     private String password;
     /**
      * sharding tag
      */
     private String shardType;

     private String refreshDNS;

}
