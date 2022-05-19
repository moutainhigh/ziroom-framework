package com.ziroom.framework.autoconfigure.web;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import com.ziroom.framework.common.api.ErrorCodes;
import com.ziroom.framework.common.api.pojo.ResponseData;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.lang.Pair.of;

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


    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<List<Map<String, Object>>> bindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        return handleBindingResult(bindingResult);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<List<Map<String, Object>>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        return handleBindingResult(bindingResult);
    }

    private ResponseData<List<Map<String, Object>>> handleBindingResult(BindingResult bindingResult) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            data.add(MapUtil.of(
                of("field", fieldError.getField()),
                of("message", fieldError.getDefaultMessage()),
                of("value", fieldError.getRejectedValue())));
        }
        for (ObjectError globalError : bindingResult.getGlobalErrors()) {
            data.add(MapUtil.of(
                of("object", globalError.getObjectName()),
                of("message", globalError.getDefaultMessage()),
                of("arguments", globalError.getArguments())));
        }
        return ResponseData.fail(ErrorCodes.BAD_REQUEST, "参数校验失败", data);
    }

    /**
     * 不支持的请求方法
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseData<Void> httpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        return ResponseData.fail(e.getMessage());
    }

    /**
     * 参数错误
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseData<Void> illegalArgumentException(HttpServletRequest request, IllegalArgumentException e) {
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
