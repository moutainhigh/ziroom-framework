package com.ziroom.framework.ferrari.repository.core.exception;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:25
 * @Version 1.0
 */
public class DaoMethodParameterException extends DaoException {

    public DaoMethodParameterException(String message) {
        super(message);
    }

    public DaoMethodParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}