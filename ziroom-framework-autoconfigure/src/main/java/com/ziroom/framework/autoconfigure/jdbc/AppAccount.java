package com.ziroom.framework.autoconfigure.jdbc;

import lombok.Data;

/**
 * @Description
 * @Author lidm
 * @Date 2020/11/5
 */
@Data
public class AppAccount {
    private String dbId;
    private String username;
    private String password;
}
