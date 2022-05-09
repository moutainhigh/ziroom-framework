package com.ziroom.ferrari.repository.core.constant;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:26
 * @Version 1.0
 */
public enum OrmFrameEnum {
    MYBATIS("mybatis"),
    JDBC_TEMPLATE("jdbcTemplate"),
    ELASTICSEARCH("elasticsearch");

    private final String value;

    OrmFrameEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
