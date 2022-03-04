package com.ziroom.framework.example.infrastructure.exception;

import com.ziroom.framework.base.CustomizeException;
import com.ziroom.framework.constant.IErrorCode;

/**
 * <p>系统级运行时异常</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public class SystemException extends CustomizeException {
    private static final long serialVersionUID = 1L;

    private int code;

    public SystemException(String message) {
        super(message);
    }

    public SystemException(int code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(IErrorCode errorCode) {
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
