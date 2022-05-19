package com.ziroom.framework.common.api.pojo;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public final class ResponseData<T> implements Serializable {

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;
    private static final long serialVersionUID = -1413678375545902711L;

    private int code;
    private String message;
    private T data;

    public static <T> ResponseData<T> success(T data) {
        return ResponseData.<T>builder().code(SUCCESS).data(data).build();
    }

    public static <T> ResponseData<T> fail(String message) {
        return ResponseData.<T>builder().code(ERROR).message(message).build();
    }

    public static <T> ResponseData<T> fail(int code, String message, T data) {
        return ResponseData.<T>builder()
                .message(message)
                .code(code)
                .data(data)
                .build();
    }

    public static <T> ResponseData<T> fail(String message, T data) {
        return ResponseData.<T>builder()
                .message(message)
                .code(ERROR)
                .data(data)
                .build();
    }

    public Boolean isSuccess() {
        return this.code == SUCCESS;
    }
}
