package com.ziroom.framework.logger;

import org.slf4j.MDC;

/**
 * <p>slf4j MDC holder</p>
 *
 * @author zhangzongqi
 * @version 1.0
 * @date Created in 2021年07月08日 15:19
 * @since 1.0
 */
public class MDCUtil {
    public static final String HEAD_TRACEID = "traceId";
    public static final String HEAD_REQUESTID = "Request-Id";

    public static void put(String key, String value) {
        MDC.put(key, value);
    }

    public static void putTraceId(String value) {
        put(HEAD_TRACEID, value);
    }

    public static String get(String key) {
        return MDC.get(key);
    }

    public static String getTraceId() {
        return get(HEAD_TRACEID);
    }

    public static void clear() {
        MDC.clear();
    }
}
