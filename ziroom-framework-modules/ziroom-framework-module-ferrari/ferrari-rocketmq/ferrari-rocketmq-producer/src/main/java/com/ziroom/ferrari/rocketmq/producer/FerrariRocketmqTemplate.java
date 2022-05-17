package com.ziroom.ferrari.rocketmq.producer;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.ziroom.ferrari.common.MessageBodyType;
import com.ziroom.ferrari.repository.common.FerrariMessageStatus;
import com.ziroom.ferrari.repository.producer.FerrariMessageDao;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import com.ziroom.ferrari.rocketmq.producer.vo.FerrariMessageVo;
import com.ziroom.ferrari.rocketmq.producer.vo.RocketmqMessageAttribute;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author: zhangzongqi
 * @Date: 2021/8/27 16:06
 * @Version 1.0
 */
public class FerrariRocketmqTemplate extends RocketMQTemplate implements InitializingBean, BeanNameAware, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(FerrariRocketmqTemplate.class);

    private FerrariMessageDao ferrariMessageDao;

    private String beanId;

    private ApplicationContext springApplicationContext;

    private static final long DEFAULT_SEND_TIMEOUT = 3000;

    private TransactionTemplate transactionTemplate;

    public static final SendResult NOT_SEND = new SendResult();

    /**
     *
     * @param topic
     * @param tags
     * @param message
     * @return
     * @param <T>
     *
     * @deprecated 使用 SendResult syncSend(String destination, Object payload)
     */
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    @Deprecated
    public <T> SendResult syncSend(String topic, String tags, T message) {
        return this.syncSendInternal(topic, tags, message, DEFAULT_SEND_TIMEOUT, null, null);
    }

    @Override
    public SendResult syncSend(String destination, Message<?> message) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        return this.syncSend(topicAndTags.getLeft(), topicAndTags.getRight(), message.getPayload());
    }

    @Override
    public SendResult syncSend(String destination, Message<?> message, long timeout) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        return this.syncSend(topicAndTags.getLeft(), topicAndTags.getRight(), message.getPayload());
    }

    @Override
    public SendResult syncSend(String destination, Message<?> message, long timeout, int delayLevel) {
        throw new UnsupportedOperationException("syncSend with delayLevel is not supported");
    }

    @Override
    public SendResult syncSend(String destination, Object payload) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        return this.syncSend(topicAndTags.getLeft(), topicAndTags.getRight(), payload);
    }

    @Override
    public SendResult syncSend(String destination, Object payload, long timeout) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        return this.syncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), payload, timeout, null, null);
    }

    @Override
    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey, long timeout) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        return this.syncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), message.getPayload(), timeout, hashKey, message.getHeaders());
    }

    @Override
    public SendResult syncSendOrderly(String destination, Object payload, String hashKey) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        return this.syncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), payload, DEFAULT_SEND_TIMEOUT, hashKey, null);
    }

    @Override
    public SendResult syncSendOrderly(String destination, Object payload, String hashKey, long timeout) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        return this.syncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), payload, timeout, hashKey, null);
    }

    @Override
    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback, long timeout) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        this.asyncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), message.getPayload(), timeout, null, message.getHeaders(), sendCallback);
    }

    @Override
    public void asyncSend(String destination, Message<?> message, SendCallback sendCallback) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        this.asyncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), message.getPayload(), DEFAULT_SEND_TIMEOUT, null, message.getHeaders(), sendCallback);
    }

    @Override
    public void asyncSend(String destination, Object payload, SendCallback sendCallback, long timeout) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        this.asyncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), payload, timeout, null, null, sendCallback);
    }

    @Override
    public void asyncSend(String destination, Object payload, SendCallback sendCallback) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        this.asyncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), payload, DEFAULT_SEND_TIMEOUT, null, null, sendCallback);
    }

    @Override
    public void asyncSendOrderly(String destination, Message<?> message, String hashKey, SendCallback sendCallback, long timeout) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        this.asyncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), message.getPayload(), DEFAULT_SEND_TIMEOUT, hashKey, message.getHeaders(), sendCallback);
    }

    @Override
    public void asyncSendOrderly(String destination, Message<?> message, String hashKey, SendCallback sendCallback) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        this.asyncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), message.getPayload(), DEFAULT_SEND_TIMEOUT, hashKey, message.getHeaders(), sendCallback);
    }

    @Override
    public void asyncSendOrderly(String destination, Object payload, String hashKey, SendCallback sendCallback) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        this.asyncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), payload, DEFAULT_SEND_TIMEOUT, hashKey, null, sendCallback);
    }

    @Override
    public void asyncSendOrderly(String destination, Object payload, String hashKey, SendCallback sendCallback, long timeout) {
        Pair<String, String> topicAndTags = parseDestination(destination);
        this.asyncSendInternal(topicAndTags.getLeft(), topicAndTags.getRight(), payload, timeout, hashKey, null, sendCallback);
    }

    public void sendAndUpdate(FerrariMessage ferrariMessage) throws ClassNotFoundException {
        FerrariMessageVo ferrariMessageVo = new FerrariMessageVo(ferrariMessage);
        String topic = ferrariMessage.getExchangeKey();
        String tags = ferrariMessage.getRoutingKey();

        String hashKey = null;
        Map<String, Object> headers = null;
        if (ferrariMessage.getMsgAttribute() != null) {
            RocketmqMessageAttribute attribute = JSON.parseObject(ferrariMessage.getMsgAttribute(), RocketmqMessageAttribute.class);
            hashKey = attribute.getHashKey();
            headers = attribute.getHeaders();
        }
        syncSendAndUpdate(topic, tags, ferrariMessageVo, hashKey, headers, DEFAULT_SEND_TIMEOUT);
    }


    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public <T> void asyncSend(String topic, String tags, T message, SendCallback callback) throws JsonProcessingException {
        FerrariMessageVo ferrariMessage = buildFerrariMessage(topic, tags, message, null, null);
        FerrariMessage entity = ferrariMessage.findEntity();
        ferrariMessageDao.insert(entity);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    asyncSendAndUpdate(topic, tags, ferrariMessage, DEFAULT_SEND_TIMEOUT, callback );
                }
            });
        } else {
            asyncSendAndUpdate(topic, tags, ferrariMessage, DEFAULT_SEND_TIMEOUT, callback);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<SendResult> batchSyncSend(String topic, String tags, List<?> messageList) throws JsonProcessingException {
        if (messageList == null || messageList.size() == 0) {
            return Collections.emptyList();
        }

        return this.transactionTemplate.execute(status -> {
            List<FerrariMessageVo> messageVoList = preSaveMessages(topic, tags, messageList);

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        batchSyncSendAndUpdate(topic, tags, messageVoList);
                    }
                });

                return Collections.emptyList();
            } else {
                return batchSyncSendAndUpdate(topic, tags, messageVoList);
            }
        });
    }

    public void batchAsyncSend(String topic, String tags, List<?> messageList, BatchSendCallback callback) throws JsonProcessingException {
        if (messageList == null || messageList.size() == 0) {
            return;
        }

        this.transactionTemplate.executeWithoutResult(transactionStatus -> {
            List<FerrariMessageVo> messageVoList = preSaveMessages(topic, tags, messageList);

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        batchAsyncSendAndUpdate(topic, tags, messageVoList, callback);
                    }
                });
            } else {
                batchAsyncSendAndUpdate(topic, tags, messageVoList, callback);
            }
        });
    }

    @Override
    public SendResult syncSendOrderly(String destination, Message<?> message, String hashKey) {
        return super.syncSendOrderly(destination, message, hashKey);
    }

    private SendResult syncSendInternal(String topic, String tags, Object message, long timeout, String hashKey, Map<String, Object> headers) {
        return this.transactionTemplate.execute(status -> {
            FerrariMessageVo ferrariMessage = buildFerrariMessage(topic, tags, message, hashKey, headers);
            FerrariMessage entity = ferrariMessage.findEntity();
            ferrariMessageDao.insert(entity);
            logger.debug("ferrari message saved: {}", ferrariMessage);

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        syncSendAndUpdate(topic, tags, ferrariMessage, hashKey, headers, timeout);
                    }
                });

                // 事务中的消息因为还没有实际发送，返回 NOT_SEND 作为占位符，避免触发空指针
                return NOT_SEND;
            } else {
                return syncSendAndUpdate(topic, tags, ferrariMessage, hashKey, headers, timeout);
            }
        });
    }

    private void asyncSendInternal(String topic, String tags, Object message, long timeout, String hashKey, Map<String, Object> headers, SendCallback callback) {
        this.transactionTemplate.executeWithoutResult(status -> {
            FerrariMessageVo ferrariMessage = buildFerrariMessage(topic, tags, message, hashKey, headers);
            FerrariMessage entity = ferrariMessage.findEntity();
            ferrariMessageDao.insert(entity);
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        asyncSendAndUpdate(topic, tags, ferrariMessage, timeout, callback);
                    }
                });
            } else {
                asyncSendAndUpdate(topic, tags, ferrariMessage, timeout, callback);
            }
        });
    }

    private List<SendResult> batchSyncSendAndUpdate(String topic, String tags, List<FerrariMessageVo> ferrariMessageList) {
        FerrariMessageStatus status = FerrariMessageStatus.SUCCESS;
        RuntimeException throwExp = null;

        List<SendResult> resultList = new ArrayList<>(ferrariMessageList.size());
        for (FerrariMessageVo ferrariMessageVo : ferrariMessageList) {
            try {
                String destination = null;
                if (StringUtils.isEmpty(tags)) {
                    destination = topic;
                } else {
                    destination = topic + ":" + tags;
                }
                resultList.add(super.syncSend(destination, ferrariMessageVo.getOriginalMessage()));
            } catch (RuntimeException e) {
                logger.error("投递消息失败, topic:{} tags:{}, message:{}", topic, tags, ferrariMessageVo, e);
                status = FerrariMessageStatus.FAILED;
                throwExp = e;
            } finally {
                updateFerrariMessage(ferrariMessageVo.findEntity(), status);
            }
        }

        if (status == FerrariMessageStatus.FAILED) {
            throw throwExp;
        }

        return resultList;
    }

    private <T> void batchAsyncSendAndUpdate(String topic, String tags, List<FerrariMessageVo> ferrariMessageList, BatchSendCallback callback) {
        FerrariMessageStatus status = FerrariMessageStatus.SUCCESS;
        RuntimeException throwExp = null;

        for (FerrariMessageVo ferrariMessageVo : ferrariMessageList) {
            try {
                String destination = null;
                if (StringUtils.isEmpty(tags)) {
                    destination = topic;
                } else {
                    destination = topic + ":" + tags;
                }
                super.asyncSend(destination, MessageBuilder.withPayload(ferrariMessageVo.getOriginalMessage()).build(), new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        updateFerrariMessage(ferrariMessageVo.findEntity(), FerrariMessageStatus.SUCCESS);
                        if (callback != null) {
                            callback.onSuccess(ferrariMessageVo.getOriginalMessage(), sendResult);
                        }
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        logger.error("投递消息失败, topic:{} tags:{}, message:{}", topic, tags, ferrariMessageVo, throwable);
                        updateFerrariMessage(ferrariMessageVo.findEntity(), FerrariMessageStatus.FAILED);
                        if (callback != null) {
                            callback.onException(ferrariMessageVo.getOriginalMessage(), throwable);
                        }
                    }
                }, DEFAULT_SEND_TIMEOUT);
            } catch (RuntimeException e) {
                logger.error("投递消息失败, topic:{} tags:{}, message:{}", topic, tags, ferrariMessageVo, e);
                status = FerrariMessageStatus.FAILED;
                throwExp = e;
                updateFerrariMessage(ferrariMessageVo.findEntity(), FerrariMessageStatus.FAILED);
            }
        }
        if (status == FerrariMessageStatus.FAILED) {
            throw throwExp;
        }
    }

    private <T> SendResult syncSendAndUpdate(String topic, String tags, FerrariMessageVo ferrariMessage, String hashKey, Map<String, Object> headers, long timeout) {
        FerrariMessageStatus status = FerrariMessageStatus.SUCCESS;
        try {
            String destination = null;
            if (StringUtils.isEmpty(tags)) {
                destination = topic;
            } else {
                destination = topic + ":" + tags;
            }

            Message<Object> message = MessageBuilder.createMessage(ferrariMessage.getOriginalMessage(), new MessageHeaders(headers));
            SendResult result;
            if (StringUtils.hasText(hashKey)) {
                result = super.syncSendOrderly(destination, message, hashKey, timeout);
            } else {
                result = super.syncSend(destination, message, timeout, 0);
            }

            logger.debug("ferrari message sent to rocketmq: {}", result);
            return result;
        } catch (Exception e) {
            status = FerrariMessageStatus.FAILED;
            throw e;
        } finally {
            updateFerrariMessage(ferrariMessage.findEntity(), status);
        }
    }

    private <T> void asyncSendAndUpdate(String topic, String tags, FerrariMessageVo ferrariMessage, long timeout, SendCallback callback) {
        try {
            String destination = null;
            if (StringUtils.isEmpty(tags)) {
                destination = topic;
            } else {
                destination = topic + ":" + tags;
            }

            super.asyncSend(destination, ferrariMessage.getOriginalMessage(), new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    updateFerrariMessage(ferrariMessage.findEntity(), FerrariMessageStatus.SUCCESS);
                    if (callback != null) {
                        callback.onSuccess(sendResult);
                    }
                }

                @Override
                public void onException(Throwable throwable) {
                    updateFerrariMessage(ferrariMessage.findEntity(), FerrariMessageStatus.FAILED);
                    if (callback != null) {
                        callback.onException(throwable);
                    }
                }
            }, timeout);
        } catch (Exception e) {
            updateFerrariMessage(ferrariMessage.findEntity(), FerrariMessageStatus.FAILED);
            throw e;
        }
    }

    private void updateFerrariMessage(FerrariMessage ferrariMessage, FerrariMessageStatus status) {
        FerrariMessage updateMessage = new FerrariMessage();
        updateMessage.setId(ferrariMessage.getId());
        updateMessage.setMsgStatus(status.getCode());
        updateMessage.setSendTime(status == FerrariMessageStatus.SUCCESS ? new Date() : null);
        try {
            ferrariMessageDao.updateSendStatus(updateMessage);
        } catch (Exception e) {
            logger.error("更新Ferrari消息失败", e);
        }
    }

    private List<FerrariMessageVo> preSaveMessages(String topic, String tags, List<?> messageList) {
        List<FerrariMessageVo> messageVoList = new ArrayList<>(messageList.size());
        List<FerrariMessage> entityList = new ArrayList<>(messageList.size());

        for (Object message : messageList) {
            FerrariMessageVo ferrariMessage = buildFerrariMessage(topic, tags, message, null, null);
            FerrariMessage entity = ferrariMessage.findEntity();
            entityList.add(entity);
            messageVoList.add(ferrariMessage);
        }
        ferrariMessageDao.batchInsert(entityList);

        return messageVoList;
    }

    private FerrariMessageVo buildFerrariMessage(String topic, String tags, Object object, String hashKey, Map<String, Object> headers) {
        FerrariMessage ferrariMessage = new FerrariMessage();
        ferrariMessage.setBeanId(beanId);
        ferrariMessage.setExchangeKey(topic);
        ferrariMessage.setRoutingKey(tags);
        ferrariMessage.setMsgType("rocketmq");
        ferrariMessage.setMsgStatus(FerrariMessageStatus.INIT.getCode());
        ferrariMessage.setMsgAttribute(JSON.toJSONString(new RocketmqMessageAttribute(hashKey, headers)));
        if (object instanceof String) {
            ferrariMessage.setMsgBodyType(MessageBodyType.String.getCode());
            ferrariMessage.setMsgBody((String) object);
        } else {
            ferrariMessage.setMsgBodyType(MessageBodyType.Object.getCode());
            ferrariMessage.setMsgBody(JSON.toJSONString(object));
        }

        ferrariMessage.setMsgBodyClass(object.getClass().getName());
        ferrariMessage.setCreateTime(new Date());
        return new FerrariMessageVo(ferrariMessage, object);
    }

    private Pair<String, String> parseDestination(String destination) {
        String[] tempArr = destination.split(":", 2);
        String topic = tempArr[0];
        String tags = null;
        if (tempArr.length > 1) {
            tags = tempArr[1];
        }
        return Pair.of(topic, tags);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.ferrariMessageDao = springApplicationContext.getBean(FerrariMessageDao.class);
        this.transactionTemplate = new TransactionTemplate(springApplicationContext.getBean(PlatformTransactionManager.class));
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);
    }

    @Override
    public void setBeanName(String beanName) {
        beanId = beanName;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        springApplicationContext = applicationContext;
    }

}
