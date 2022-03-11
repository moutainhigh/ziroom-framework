package com.ziroom.framework.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>请求返回结果</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class ResponseData<T> implements Serializable, IResponseData<T> {

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;
    private static final long serialVersionUID = -1413678375545902711L;
    private int code;
    private String message;
    private T data;

    public static ResponseData ok(Object data) {
        return ResponseData.builder().code(SUCCESS).data(data).build();
    }

    public static ResponseData fail(String message) {
        return ResponseData.builder().code(ERROR).message(message).build();
    }

    public static <T> ResponseData<T> fail(int code, String message, T data) {
        ResponseData<T> responseData = new ResponseData<T>();
        responseData.setCode(code);
        responseData.setData(data);
        responseData.setMessage(message);
        return responseData;
    }

    public static <T> ResponseData<T> fail(String message, T data) {
        ResponseData<T> responseData = new ResponseData<T>();
        responseData.setCode(ERROR);
        responseData.setData(data);
        responseData.setMessage(message);
        return responseData;
    }

    public static <T> ResponseData<T> fail(T data) {
        ResponseData<T> responseData = new ResponseData<T>();
        responseData.setCode(ERROR);
        responseData.setData(data);
        return responseData;
    }

    public static <T> ResponseData<T> success(T data) {
        ResponseData<T> responseData = new ResponseData<T>();
        responseData.setCode(SUCCESS);
        responseData.setData(data);
        return responseData;
    }

    @Override
    public Boolean isSuccess() {
        return this.code == SUCCESS;
    }
}
