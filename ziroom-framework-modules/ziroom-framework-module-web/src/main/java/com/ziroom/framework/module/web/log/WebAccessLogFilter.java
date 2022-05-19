package com.ziroom.framework.module.web.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: lijun
 * @Date: 2020/2/19 17:53
 */
@RequiredArgsConstructor
public class WebAccessLogFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger("ziroom.web.accesslog");

    private final WebAccessLogProperties logProperties;

    private final ObjectMapper objectMapper;

    /**
     * 时间格式化
     */
    private static final String JSON_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 实现 日志过滤
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //处理当前请求日志
        long beginTime = System.currentTimeMillis();
        String requestUrl = request.getRequestURI();
        String contextPath = request.getContextPath();
        String simpleUrl = requestUrl.substring(contextPath.length());
        String requestBody = "";
        String contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);

        if (logProperties.isIncludeRequestBody()) {
            if (!requestUrl.contains(".") && Objects.nonNull(contentType)) {
                //静态请求，不处理
                if (contentType.startsWith(MediaType.APPLICATION_JSON_VALUE) || contentType.startsWith(MediaType.APPLICATION_XML_VALUE)) {
                    //JSON xml Rest 请求
                    request = new ContentCachingRequestWrapper(request, logProperties.getMaxRequestBodyLength());
//                    requestBody = requestWrapper.getBody();
//                    request = requestWrapper;
                } else if (contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE)) {
                    //application/x-www-form-urlencoded 表单提交
                    requestBody = objectMapper.writeValueAsString(request.getParameterMap());
                } else if (contentType.startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
                    //multipart/form-data 文件上传
                    requestBody = getFormParam(request);
                }
            } else if (Objects.isNull(contentType)) {
                requestBody = objectMapper.writeValueAsString(request.getParameterMap());
            }
        } else {
            requestBody = "-";
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            final long timeCost = System.currentTimeMillis() - beginTime;
            if (request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper cachingRequestWrapper = (ContentCachingRequestWrapper) request;
                requestBody = new String(cachingRequestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8)
                    .replaceAll("\\s+", " ")
                    .replace('\n', ' ')
                    .replace('\t', ' ');
            }

            int status = response.getStatus();
            if (timeCost >= logProperties.getTp95Threshold().toMillis()) {
                //输出日志信息
                LOGGER.warn("{}\t| {}\t | {}\t| {}\t| {}\t| {}\t| {}ms",
                    request.getMethod(), simpleUrl, getHeadInfo(request), contentType, requestBody, status, timeCost);

            } else {
                //输出日志信息
                LOGGER.info("{}\t| {}\t | {}\t| {}\t| {}\t| {}\t| {}ms",
                    request.getMethod(), simpleUrl, getHeadInfo(request), contentType, requestBody, status, timeCost);
            }
        }
    }

    /**
     * 获取请求头信息
     */
    private String getHeadInfo(HttpServletRequest request) {
        if (logProperties.getIncludeHeaders().isEmpty()) {
            return null;
        }
        Map<String, Object> headerInfo = new HashMap<>(logProperties.getIncludeHeaders().size());
        for (String key : logProperties.getIncludeHeaders()) {
            String header = request.getHeader(key);
            if (StringUtils.isNotBlank(header)) {
                headerInfo.put(key, header);
            }
        }
        try {
            return objectMapper.writeValueAsString(headerInfo);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    /**
     * 获取表单数据
     */
    private String getFormParam(HttpServletRequest request) {
        MultipartResolver resolver = new StandardServletMultipartResolver();
        MultipartHttpServletRequest mRequest = resolver.resolveMultipart(request);

        Map<String, Object> param = new HashMap<>();
        Map<String, String[]> parameterMap = mRequest.getParameterMap();
        if (!parameterMap.isEmpty()) {
            param.putAll(parameterMap);
        }
        Map<String, MultipartFile> fileMap = mRequest.getFileMap();
        if (!fileMap.isEmpty()) {
            for (Map.Entry<String, MultipartFile> fileEntry : fileMap.entrySet()) {
                MultipartFile file = fileEntry.getValue();
                param.put(fileEntry.getKey(), file.getOriginalFilename() + "(" + file.getSize() + " byte)");
            }
        }
        try {
            return objectMapper.writeValueAsString(param);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

}
