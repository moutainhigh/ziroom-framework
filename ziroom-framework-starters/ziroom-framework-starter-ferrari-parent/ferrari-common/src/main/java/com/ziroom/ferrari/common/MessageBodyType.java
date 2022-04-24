package com.ziroom.ferrari.common;

/**
 * @Author: J.T.
 * @Date: 2021/11/9 10:06
 * @Version 1.0
 */
public enum MessageBodyType {

    String(1),
    Object(2);


    private int code;

    MessageBodyType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
