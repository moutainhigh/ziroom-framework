package com.ziroom.framework.example.infrastructure.common;

import com.ziroom.framework.api.pojo.ResponseData;
import com.ziroom.framework.base.CustomizeException;
import com.ziroom.framework.constant.IGlobalExceptionHandler;
import com.ziroom.framework.example.infrastructure.exception.BusinessException;
import com.ziroom.framework.example.infrastructure.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler implements IGlobalExceptionHandler {

    /**
     * 处理自定义的业务异常
     *
     * @param e
     * @return
     */
    @Override
    @ExceptionHandler(value = CustomizeException.class)
    @ResponseBody
    public ResponseData customizeExceptionHandler(CustomizeException e) {
        log.error("发生业务异常！原因是：{}", e.getMessage(), e);
        return ResponseData.fail(e.getCode(), e.getMessage(), null);
    }

    /**
     * 处理空指针的异常
     *
     * @param e
     * @return
     */
    @Override
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResponseData exceptionHandler(NullPointerException e) {
        log.error("发生空指针异常！原因是：{}", e.getMessage(), e);
        return ResponseData.fail(ErrorCode.ERROR_DEFAULT_CODE, e.getMessage(), null);
    }

    /**
     * 处理其他异常
     *
     * @param e
     * @return
     */
    @Override
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseData exceptionHandler(Exception e) {
        log.error("发生业务异常！原因是：{}", e.getMessage(), e);
        return ResponseData.fail(ErrorCode.ERROR_DEFAULT_CODE, e.getMessage(), null);
    }

    /**
     * 处理自定义的业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ResponseData bizExceptionHandler(BusinessException e) {
        log.error("发生业务异常！原因是：{}", e.getMessage(), e);
        return ResponseData.fail(e.getCode(), e.getMessage(), null);
    }


    @ExceptionHandler(value = SystemException.class)
    @ResponseBody
    public ResponseData sysExceptionHandler(SystemException e) {
        log.error("发生系统异常！原因是：{}", e.getMessage(), e);
        return ResponseData.fail(e.getCode(), e.getMessage(), null);
    }
}
