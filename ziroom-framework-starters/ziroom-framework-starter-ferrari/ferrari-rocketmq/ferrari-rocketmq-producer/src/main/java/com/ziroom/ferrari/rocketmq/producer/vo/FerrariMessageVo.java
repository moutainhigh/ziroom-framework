package com.ziroom.ferrari.rocketmq.producer.vo;

import com.alibaba.fastjson.JSON;
import com.ziroom.ferrari.common.MessageBodyType;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: J.T.
 * @Date: 2021/11/9 10:34
 * @Version 1.0
 */
public class FerrariMessageVo {

    private FerrariMessage ferrariMessage;

    private transient Object originalMessage;


    public FerrariMessageVo() {
    }

    public FerrariMessageVo(FerrariMessage ferrariMessage) throws ClassNotFoundException {
        this.ferrariMessage = ferrariMessage;
        if (MessageBodyType.String.getCode() == ferrariMessage.getMsgBodyType()) {
            this.originalMessage = ferrariMessage.getMsgBody();
        } else if (StringUtils.isNotBlank(ferrariMessage.getMsgBodyClass())) {
            Class<?> clazz = Class.forName(ferrariMessage.getMsgBodyClass());
            this.originalMessage = JSON.parseObject(ferrariMessage.getMsgBody(), clazz);
        } else {
            this.originalMessage = JSON.parse(ferrariMessage.getMsgBody());
        }
    }

    public FerrariMessageVo(FerrariMessage ferrariMessage, Object originalMessage) {
        this.ferrariMessage = ferrariMessage;
        this.originalMessage = originalMessage;
    }

    public FerrariMessage findEntity() {
        return ferrariMessage;
    }

    public Object getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(Object originalMessage) {
        this.originalMessage = originalMessage;
    }
}
