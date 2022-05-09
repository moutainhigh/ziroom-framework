package com.ziroom.ferrari.repository.common;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 15:18
 * @Version 1.0
 */
public enum FerrariMessageStatus {
    INIT(1, "新建"),
    SUCCESS(2, "成功"),
    FAILED(3, "失败"),
    FAILED_NOT_RETRY(4, "失败不重试");

    private int code;

    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    FerrariMessageStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
