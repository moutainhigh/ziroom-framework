package com.ziroom.framework.util;

import com.ziroom.framework.base.DataErrorException;
import com.ziroom.framework.base.RemoteHttpException;
import com.ziroom.framework.base.RpcServiceException;
import com.ziroom.framework.constant.ExceptionTypeEnity;
import com.ziroom.framework.constant.LogPrint;

/**
 * <p>异常工具类</p>
 *
 * @author zhangzongqi
 * @Date Created in 2021年07月01日 14:45
 */
public class ExceptionUtil {

    private static final String DATAERRORMESSAGE = "数据错误";

    private ExceptionUtil() {
    }

    public static void checkDataError() {
        throw new DataErrorException(DATAERRORMESSAGE);
    }

    /**
     * <p>数据验证不通过(为false时)，抛出异常</p>
     *
     * @param expression
     */
    public static void checkDataError(boolean expression) {
        if (!expression) {
            throw new DataErrorException(DATAERRORMESSAGE);
        }
    }

    public static void checkDataError(boolean expression, String message, Object... args) {
        checkDataError(expression, null, null, DATAERRORMESSAGE, args);
    }

    public static void checkDataError(boolean expression, LogPrint logPrint) {
        checkDataError(expression, logPrint, null, DATAERRORMESSAGE, null);
    }

    public static void checkDataError(boolean expression, ExceptionTypeEnity enity) {
        checkDataError(expression, null, enity, DATAERRORMESSAGE, null);
    }

    public static void checkDataError(boolean expression, LogPrint logPrint, ExceptionTypeEnity enity, String message, Object... args) {

        if (!expression) {
            if (logPrint != null) {
                logPrint.log();
            }
            throw new DataErrorException(message, enity);
        }
    }

    public static void checkDataError(boolean expression, int code, LogPrint logPrint, ExceptionTypeEnity enity, String message, Object... args) {

        if (!expression) {
            if (logPrint != null) {
                logPrint.log();
            }
            throw new DataErrorException(code, message, enity);
        }
    }

    public static void checkRemoteHTTP(boolean expression, int code, LogPrint logPrint, ExceptionTypeEnity enity, String message, Object... args) {

        if (!expression) {
            if (logPrint != null) {
                logPrint.log();
            }
            throw new RemoteHttpException(code, message, enity);
        }
    }

    public static void checkRpcService(boolean expression, int code, LogPrint logPrint, ExceptionTypeEnity enity, String message, Object... args) {

        if (!expression) {
            if (logPrint != null) {
                logPrint.log();
            }
            throw new RpcServiceException(code, message, enity);
        }
    }
}