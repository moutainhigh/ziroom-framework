package com.ziroom.ferrari.repository.core.exception;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:23
 * @Version 1.0
 */
public class DaoException extends RuntimeException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}