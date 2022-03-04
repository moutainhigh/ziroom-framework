package com.ziroom.framework.dubbo.filter;

import com.ziroom.framework.logger.MDCUtil;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.util.StringUtils;

/**
 * <p>dubbo的消费者traceId</p>
 *
 * @author zhangzongqi
 * @version 1.0
 * @date Created in 2021年07月08日 15:19
 */
public abstract class AbstractConsumerSideLogFilter {
    public void setTraceId(String traceIdVal) {
        if (StringUtils.isEmpty(traceIdVal)) {
            traceIdVal = MDCUtil.getTraceId();
        }
        RpcContext.getContext().setAttachment(MDCUtil.HEAD_TRACEID, traceIdVal);
    }
}
