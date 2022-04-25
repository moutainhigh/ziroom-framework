package com.ziroom.framework.ferrari.repository.core.exception;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:25
 * @Version 1.0
 */
public class DaoExceptionTranslator {

    private DaoExceptionTranslator() {
    }

    public static DaoException translate(Exception ex) {
        if (ex instanceof IllegalArgumentException) {
            return new DaoMethodParameterException("Dao Param Exception[" + ex.getMessage() + "]");
        } else if (ex instanceof DaoException) {
            return (DaoException) ex;
        } else {
            return new DaoException("Dao Unknown Exception[" + ex.getMessage() + "]", ex);
        }
    }
}

