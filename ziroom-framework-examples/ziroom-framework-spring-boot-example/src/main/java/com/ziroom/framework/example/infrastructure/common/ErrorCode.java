package com.ziroom.framework.example.infrastructure.common;


import com.ziroom.framework.constant.IErrorCode;

import java.io.Serializable;

/**
 * <p>系统错误码</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public class ErrorCode implements Serializable, IErrorCode {
    public static final int ERROR_DEFAULT_CODE = 500;
    private static final long serialVersionUID = 1L;
    private int code;
    private String message;

    public ErrorCode() {

    }

    public ErrorCode(String message) {
        this.code = ERROR_DEFAULT_CODE;
        this.message = message;
    }

    public ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorCode ofError(String message) {
        return new ErrorCode(ERROR_DEFAULT_CODE, message);
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

