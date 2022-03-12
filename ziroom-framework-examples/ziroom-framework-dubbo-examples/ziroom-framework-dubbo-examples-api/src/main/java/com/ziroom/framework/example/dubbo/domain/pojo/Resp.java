package com.ziroom.framework.example.dubbo.domain.pojo;

import com.ziroom.framework.common.api.pojo.IResponseData;
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
public final class Resp<T> implements Serializable, IResponseData<T> {

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;
    private static final long serialVersionUID = -1413678375545902711L;
    private int code;
    private String message;
    private T data;

    public static Resp ok(Object data) {
        return Resp.builder().code(SUCCESS).data(data).build();
    }

    public static Resp fail(String message) {
        return Resp.builder().code(ERROR).message(message).build();
    }

    public static <T> Resp<T> fail(int code, String message, T data) {
        Resp<T> responseData = new Resp<T>();
        responseData.setCode(code);
        responseData.setData(data);
        responseData.setMessage(message);
        return responseData;
    }

    public static <T> Resp<T> fail(String message, T data) {
        Resp<T> responseData = new Resp<T>();
        responseData.setCode(ERROR);
        responseData.setData(data);
        responseData.setMessage(message);
        return responseData;
    }

    public static <T> Resp<T> fail(T data) {
        Resp<T> responseData = new Resp<T>();
        responseData.setCode(ERROR);
        responseData.setData(data);
        return responseData;
    }

    public static <T> Resp<T> success(T data) {
        Resp<T> responseData = new Resp<T>();
        responseData.setCode(SUCCESS);
        responseData.setData(data);
        return responseData;
    }

    @Override
    public Boolean isSuccess() {
        return this.code == SUCCESS;
    }
}
