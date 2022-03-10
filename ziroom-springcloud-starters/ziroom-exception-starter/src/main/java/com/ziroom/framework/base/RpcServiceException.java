package com.ziroom.framework.base;

import com.ziroom.framework.constant.ExceptionTypeEnity;

/**
 * <p>RPC远程请求异常</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public class RpcServiceException extends CustomizeException {

    private static final long serialVersionUID = -8400140466566314354L;

    public RpcServiceException() {
        super();
    }

    public RpcServiceException(String message) {
        super(message);
    }

    public RpcServiceException(String message, Throwable cause) {
        super(message, cause, null);
    }

    public RpcServiceException(int code, String message, ExceptionTypeEnity enity) {
        super(code, message, enity);
    }
}
