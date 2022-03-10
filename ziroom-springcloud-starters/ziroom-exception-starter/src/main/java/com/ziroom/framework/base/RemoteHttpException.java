package com.ziroom.framework.base;

import com.ziroom.framework.constant.ExceptionTypeEnity;

/**
 * <p>HTTP远程请求异常</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public class RemoteHttpException extends CustomizeException {

    private static final long serialVersionUID = 1842522620741050883L;

    public RemoteHttpException() {
        super();
    }

    public RemoteHttpException(String message) {
        super(message);
    }

    public RemoteHttpException(String message, Throwable cause) {
        super(message, cause, null);
    }

    public RemoteHttpException(int code, String message, ExceptionTypeEnity enity) {
        super(code, message, enity);
    }
}
