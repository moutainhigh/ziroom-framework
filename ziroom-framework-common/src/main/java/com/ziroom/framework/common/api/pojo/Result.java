package com.ziroom.framework.common.api.pojo;

public class Result<T> {
    private boolean isSuccess;
    private Integer code;
    private String message;
    private T data;
    private boolean rollback = false;

    private Result() {
    }

    public Result(boolean isSuccess, Integer code, String message, T data) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public Result<T> setSuccess(boolean success) {
        this.isSuccess = success;
        return this;
    }

    public Integer getCode() {
        return this.code;
    }

    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return this.data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public boolean isRollback() {
        return this.rollback;
    }

    public Result<T> setRollback(boolean rollback) {
        this.rollback = rollback;
        return this;
    }

    public static Result getInstance() {
        return new Result();
    }
}
