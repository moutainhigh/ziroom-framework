package com.ziroom.ferrari.repository.core.constant;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:45
 * @Version 1.0
 */
public enum DialectEnum {
    MYSQL("mysql"),
    ORACLE("oracle"),
    ELASTICSEARCH("elasticsearch");

    private final String value;

    DialectEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}