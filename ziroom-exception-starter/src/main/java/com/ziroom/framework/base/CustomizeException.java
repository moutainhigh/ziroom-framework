package com.ziroom.framework.base;

import com.ziroom.framework.constant.ExceptionTypeEnity;
import com.ziroom.framework.constant.IErrorCode;
import lombok.Data;

/**
 * <p>自定义业务的运行时异常</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
@Data
public class CustomizeException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;

    private ExceptionTypeEnity enity;

    public CustomizeException() {
        super();
    }

    public CustomizeException(String message) {
        super(message);
    }

    public CustomizeException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CustomizeException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public CustomizeException(int code, String message, ExceptionTypeEnity enity) {
        super(message);
        this.code = code;
        this.enity = enity;
    }

    public CustomizeException(String message, Throwable cause, ExceptionTypeEnity enity) {
        super(message, cause);
        this.enity = enity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("{");
        sb.append("code=").append(getCode());
        sb.append(",");
        sb.append("message=").append(getLocalizedMessage());
        sb.append("}");
        return sb.toString();
    }
}
