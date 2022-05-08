package com.ziroom.framework.autoconfigure.web;

import com.ziroom.framework.common.api.pojo.ResponseData;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Created by liangrk on 2022/5/5.
 */
@RestControllerAdvice
@Slf4j
public class DefaultExceptionHandler {

    /**
     * 缺少必要的参数
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Void> missingParameterHandler(HttpServletRequest request, MissingServletRequestParameterException e) {
        this.logError(request, e);
        return ResponseData.fail(e.getMessage());
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Void> methodArgumentTypeMismatchException(HttpServletRequest request, MethodArgumentTypeMismatchException e) {
        this.logError(request, e);
        return ResponseData.fail(e.getMessage());
    }

    /**
     * 不支持的请求方法
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseData<Void> httpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        this.logError(request, e);
        return ResponseData.fail(e.getMessage());
    }

    /**
     * 参数错误
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Void> illegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
        this.logError(request, e);
        return ResponseData.fail(e.getMessage());
    }


    /**
     * 其他异常统一处理
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Void> exception(HttpServletRequest request, Exception e) {
        this.logError(request, e);
        return ResponseData.fail(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseData<Void> handleRuntimeException(HttpServletRequest request, RuntimeException e) {
        this.logError(request, e);
        return ResponseData.fail(e.getMessage());
    }


    /**
     * 记录错误日志
     */
    private void logError(HttpServletRequest request, Exception e) {
        log.error("path:{}, queryParam:{}, errorMessage:{}", request.getRequestURI(), request.getQueryString(), e.getMessage(), e);
    }


}
