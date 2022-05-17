package com.ziroom.ferrari.test.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @Author: wangsq8
 * @CreateTime: 2021-07-08 16:18
 * @Description:
 */
@Slf4j
@Service
public class RocketMqExampleListener implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        try {
            for (MessageExt messageExt : list) {
                handelMessage(messageExt);
            }
        } catch (Exception e) {
            log.error("消息变更Mq处理系统异常,e:{}", e);
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }

    private void handelMessage(MessageExt messageExt) {
        String data = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("消息变更Mq通知: [{}]", data);
    }
}
