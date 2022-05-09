package com.ziroom.ferrari.repository.consumer.entity;

import com.ziroom.ferrari.repository.core.annotation.Column;
import com.ziroom.ferrari.repository.core.annotation.Table;
import com.ziroom.ferrari.repository.core.entity.IdEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 9:51
 * @Version 1.0
 */


@Table(value = "ferrari_message_consumer")
public class FerrariMessageConsumer extends IdEntity implements Serializable {


    @Column(value = "msg_id")
    private String msgId;

    @Column(value = "msg_type")
    private String msgType;

    @Column(value = "exchange_key")
    private String exchangeKey;

    @Column(value = "routing_key")
    private String routingKey;

    @Column(value = "queue")
    private String queue;

    @Column(value = "msg_body")
    private String msgBody;

    @Column(value = "create_time")
    private Date createTime;


    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getExchangeKey() {
        return exchangeKey;
    }

    public void setExchangeKey(String exchangeKey) {
        this.exchangeKey = exchangeKey;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
