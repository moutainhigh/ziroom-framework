package com.ziroom.framework.dubbo.filter;

import com.ziroom.framework.logger.MDCUtil;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.util.StringUtils;

/**
 * <p>dubbo的生产者traceId</p>
 *
 * @author zhangzongqi
 * @version 1.0
 * @date Created in 2021年07月08日 15:19
 */
public abstract class AbstractProviderSideLogFilter {
    public void setTraceId() {
        String traceIdVal = RpcContext.getContext().getAttachment(MDCUtil.HEAD_TRACEID);
        if (StringUtils.isEmpty(traceIdVal)) {
            MDCUtil.putTraceId(MDCUtil.getTraceId());
        } else {
            MDCUtil.putTraceId(traceIdVal);
        }
    }
}
