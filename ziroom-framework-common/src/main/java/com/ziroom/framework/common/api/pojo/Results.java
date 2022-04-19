package com.ziroom.framework.common.api.pojo;

import java.io.Serializable;

public class Results implements Serializable {
    public static final int SUCCESS = 2000;
    public static final String DEFAULT_SUCCESS_MESSAGE = "响应成功";
    public static final int FAILURE = 5000;
    public static final String DEFAULT_FAILURE_MESSAGE = "系统异常";

    public Results() {
    }

    public static <T> Result success() {
        return success((Object)null);
    }

    public static <T> Result success(T data) {
        return Result.getInstance().setSuccess(true).setCode(2000).setMessage("响应成功").setData(data);
    }

    public static <T> Result failure() {
        return failure(5000, "系统异常", (Object)null);
    }

    public static <T> Result failure(String message) {
        return failure(5000, message, (Object)null);
    }

    public static <T> Result failure(Integer code, String message) {
        return failure(code, message, (Object)null);
    }

    public static <T> Result failure(Integer code, String message, T data) {
        return Result.getInstance().setSuccess(false).setCode(code).setMessage(message).setData(data);
    }
}