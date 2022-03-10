package com.ziroom.framework.constant;

import com.ziroom.framework.base.CustomizeException;

/**
 * 全局异常处理行为
 */
public interface IGlobalExceptionHandler {
    /**
     * 处理自定义的业务异常
     *
     * @param e
     * @return
     */
    <T> T customizeExceptionHandler(CustomizeException e);

    /**
     * 处理空指针的异常
     *
     * @param e
     * @return
     */
    <T> T exceptionHandler(NullPointerException e);


    /**
     * 处理其他异常
     *
     * @param e
     * @return
     */
    <T> T exceptionHandler(Exception e);
}
