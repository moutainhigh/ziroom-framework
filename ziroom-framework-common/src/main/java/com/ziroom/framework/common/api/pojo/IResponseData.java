package com.ziroom.framework.common.api.pojo;

/**
 * <p>定义请求返回的行为</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public interface IResponseData<T> {

    static IResponseData ok(Object data) {
        return null;
    }

    static ResponseData fail(String message) {
        return null;
    }

    static <T> ResponseData<T> fail(int code, String message, T data) {
        return null;
    }

    static <T> ResponseData<T> fail(String message, T data) {
        return null;
    }

    static <T> ResponseData<T> fail(T data) {
        return null;
    }

    static <T> ResponseData<T> success(T data) {
        return null;
    }

    Boolean isSuccess();
}
