package com.ziroom.framework.common.exception;

/**
 * Created by liangrk on 2022/4/27.
 */
public class InstantiationRuntimeException extends RuntimeException {

    public InstantiationRuntimeException() {
    }

    public InstantiationRuntimeException(String message) {
        super(message);
    }

    public InstantiationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstantiationRuntimeException(Throwable cause) {
        super(cause);
    }

    public InstantiationRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
