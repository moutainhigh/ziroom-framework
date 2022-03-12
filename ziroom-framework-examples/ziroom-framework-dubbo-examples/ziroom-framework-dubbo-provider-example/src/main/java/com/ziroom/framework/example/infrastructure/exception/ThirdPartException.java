package com.ziroom.framework.example.infrastructure.exception;

import com.ziroom.framework.base.CustomizeException;
import com.ziroom.framework.constant.IErrorCode;

/**
 * <p>三方平台调用异常</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public class ThirdPartException extends CustomizeException {
    private static final long serialVersionUID = 1L;

    private int code;

    public ThirdPartException(String message) {
        super(message);
    }

    public ThirdPartException(int code, String message) {
        super(message);
        this.code = code;
    }

    public ThirdPartException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getName());
        sb.append("{");
        sb.append("code=").append(getCode());
        sb.append(",");
        sb.append("message=").append(getLocalizedMessage());
        sb.append('}');
        return sb.toString();
    }
}
