package com.ziroom.framework.http.filter;

import com.ziroom.framework.logger.MDCUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class HttpUtils {
    public static String getTraceId(HttpServletRequest req) {
        try {
            String traceId = null;
            if (req != null) {
                traceId = req.getHeader(MDCUtil.HEAD_TRACEID);
                if (traceId == null || "".equals(traceId)) {
                    traceId = req.getHeader(MDCUtil.HEAD_REQUESTID);
                }
                if (traceId == null || "".equals(traceId)) {
                    traceId = UUID.randomUUID().toString();
                }
            }
            return traceId;
        } catch (Throwable e) {
            return UUID.randomUUID().toString();
        }
    }

    public static void setTraceId(HttpServletResponse response, String traceId) {
        response.setHeader(MDCUtil.HEAD_TRACEID, traceId);
    }


}
