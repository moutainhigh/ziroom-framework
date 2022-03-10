package com.ziroom.framework.example.infrastructure.common;


import com.ziroom.framework.constant.IErrorCode;

/**
 * <p>系统错误码</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public enum ErrorCodeEnums implements IErrorCode {
    ERROR_DEFAULT_CODE(500, "ERROR");

    private int code;
    private String errorMessage;

    ErrorCodeEnums(int code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}

