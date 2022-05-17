package com.ziroom.ferrari.test.listener;

import com.ziroom.ferrari.test.dto.NoticeReq;
import com.ziroom.ferrari.test.service.TestRocketmqService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @Author: wangsq8
 * @CreateTime: 2021-07-08 16:18
 * @Description:
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = TestRocketmqService.NOTICE_TOPIC,
        consumerGroup = TestRocketmqService.CONSUMER_GROUP_LISTENTER
)
public class RocketMqExampleAnnotionListener<T> implements RocketMQListener<NoticeReq> {

    @Override
    public void onMessage(NoticeReq noticeReq) {
        try {
            handelMessage(noticeReq);
        } catch (Exception e) {
            log.error("消息变更Mq处理系统异常,e:{}", e);
        }
    }

    private void handelMessage(NoticeReq noticeReq) {
//        String data = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("消息变更Mq通知: [{}]", noticeReq);
    }
}
