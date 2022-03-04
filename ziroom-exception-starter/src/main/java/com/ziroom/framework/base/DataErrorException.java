package com.ziroom.framework.base;

import com.ziroom.framework.constant.ExceptionTypeEnity;

/**
 * <p>数据验证类异常</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public class DataErrorException extends CustomizeException {

    private static final long serialVersionUID = 9182333565097628064L;

    public DataErrorException() {
        super();
    }

    public DataErrorException(String message) {
        super(message);
    }

    public DataErrorException(String message, Throwable cause) {
        super(message, cause, null);
    }

    public DataErrorException(String message, ExceptionTypeEnity enity) {
        super(message, null, enity);
    }

    public DataErrorException(int code, String message, ExceptionTypeEnity enity) {
        super(code, message, enity);
    }
}