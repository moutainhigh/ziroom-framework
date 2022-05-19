package com.ziroom.framework.module.web.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ziroom.framework.common.api.pojo.ResponseData;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class EnforceResponseDataAdvice implements ResponseBodyAdvice<Object>, Ordered {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher("/");
    private final List<MatchAction> excludePaths = new ArrayList<>();
    private final Set<Class<?>> excludeTypes;

    private final ObjectMapper stringResponseMapper;

    public EnforceResponseDataAdvice(WebResponseProperties properties, ObjectMapper stringResponseMapper) {
        WebResponseProperties.Enforce enforce = properties.getEnforce();
        this.stringResponseMapper = stringResponseMapper;
        for (String excludePath : enforce.getExcludePaths()) {
            excludePaths.add(new MatchAction(!antPathMatcher.isPattern(excludePath), excludePath));
        }

        this.excludeTypes = enforce.getExcludeTypes();
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        NoEnvelope skipMethod = returnType.getMethodAnnotation(NoEnvelope.class);
        if (skipMethod != null) {
            return false;
        }

        boolean skipController = returnType.getDeclaringClass().isAnnotationPresent(NoEnvelope.class);
        if (skipController) {
            return false;
        }

        if (typeExcluded(returnType)) {
            return false;
        }

        return true;
    }

    private boolean typeExcluded(MethodParameter returnType) {
        return returnType.getMethod() != null && excludeTypes.contains(returnType.getMethod().getReturnType());
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (pathExcluded(request)) {
            return body;
        }

        if (body == null) {
            return ResponseData.success(null);
        }

        if (body instanceof ResponseData<?> || body instanceof Throwable) {
            return body;
        }

        // 方法直接返回 String 类型时，会被 org.springframework.http.converter.StringHttpMessageConverter 处理
        // 不能直接包裹 ResponseData 返回，否则会抛出 ClassCastException
        if (body instanceof CharSequence) {
            try {
                return stringResponseMapper.writeValueAsString(ResponseData.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return ResponseData.success(body);
    }

    private boolean pathExcluded(ServerHttpRequest request) {
        for (MatchAction action : excludePaths) {
            String requestPath = request.getURI().getPath();
            if (action.exact && requestPath.equals(action.path)) {
                return true;
            } else if (!action.exact && antPathMatcher.match(action.path, requestPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @AllArgsConstructor
    static class MatchAction {
        boolean exact;
        String path;
    }
}
