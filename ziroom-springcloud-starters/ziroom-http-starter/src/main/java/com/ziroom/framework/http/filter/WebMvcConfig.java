package com.ziroom.framework.http.filter;

import com.ziroom.framework.logger.MDCUtil;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器配置
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TraceInterceptor()).addPathPatterns("/**");
        super.addInterceptors(registry);
    }

    public class TraceInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            String traceIdVal = HttpUtils.getTraceId(request);
            if (!StringUtils.isEmpty(traceIdVal)) {
                MDC.put(MDCUtil.HEAD_TRACEID, traceIdVal);
            } else {
                MDC.remove(MDCUtil.HEAD_TRACEID);
            }
            HttpUtils.setTraceId(response, traceIdVal);
            return true;
        }
    }
}