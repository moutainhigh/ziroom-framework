package com.ziroom.framework.common.exception;

/**
 * Created by liangrk on 2022/4/27.
 */
public class IllegalAccessRuntimeException extends RuntimeException{

    public IllegalAccessRuntimeException() {
    }

    public IllegalAccessRuntimeException(String message) {
        super(message);
    }

    public IllegalAccessRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalAccessRuntimeException(Throwable cause) {
        super(cause);
    }

    public IllegalAccessRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
