package com.ziroom.framework.common.api.pojo;

import java.io.Serializable;

public class Results implements Serializable {
    public static final int SUCCESS = 2000;
    public static final String DEFAULT_SUCCESS_MESSAGE = "响应成功";
    public static final int FAILURE = 5000;
    public static final String DEFAULT_FAILURE_MESSAGE = "系统异常";

    public Results() {
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(true, SUCCESS, DEFAULT_SUCCESS_MESSAGE, data);
    }

    public static <T> Result<T> failure() {
        return failure(FAILURE, DEFAULT_FAILURE_MESSAGE, null);
    }

    public static <T> Result<T> failure(String message) {
        return failure(FAILURE, message, null);
    }

    public static <T> Result<T> failure(Integer code, String message) {
        return failure(code, message, null);
    }

    public static <T> Result<T> failure(Integer code, String message, T data) {
        return new Result<>(false, code, message, data);
    }
}
