package com.ziroom.framework.modules.dubhe.rocketmq;

import com.ziroom.tech.ares.context.RequestContext;
import com.ziroom.tech.ares.context.RequestContextFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByRandom;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.List;

public class DubheMessageQueueSelector implements MessageQueueSelector {

    private MessageQueueSelector defaultSelector = new SelectMessageQueueByRandom();


    @Override
    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
        RequestContext requestContext = RequestContextFactory.INSTANCE.current();
        if (requestContext == null) {
            return defaultSelector.select(mqs, msg, arg);
        }

        String currentEnv = requestContext.getEnvName();
        if (StringUtils.isBlank(currentEnv)) {
            return defaultSelector.select(mqs, msg, arg);
        }

        List<MessageQueue> stableQueues = new ArrayList<>();
        List<MessageQueue> envQueues = new ArrayList<>();
        for (MessageQueue messageQueue : mqs) {
            if (messageQueue.getBrokerName().startsWith("stable-")) {
                stableQueues.add(messageQueue);
            } else if (messageQueue.getBrokerName().startsWith(currentEnv)) {
                envQueues.add(messageQueue);
            }
        }

        if (stableQueues.isEmpty() && envQueues.isEmpty()) {
            return defaultSelector.select(mqs, msg, arg);
        }

        return defaultSelector.select(envQueues.isEmpty() ? stableQueues : envQueues, msg, arg);
    }
    
}
