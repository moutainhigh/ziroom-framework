package com.ziroom.framework.ferrari.repository.producer.entity;

import com.ziroom.framework.ferrari.repository.core.annotation.Column;
import com.ziroom.framework.ferrari.repository.core.annotation.Table;
import com.ziroom.framework.ferrari.repository.core.entity.IdEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 9:51
 * @Version 1.0
 */


@Table(value = "ferrari_message")
public class FerrariMessage extends IdEntity implements Serializable {


    @Column(value = "bean_id")
    private String beanId;

    @Column(value = "exchange_key")
    private String exchangeKey;

    @Column(value = "routing_key")
    private String routingKey;

    @Column(value = "msg_status")
    private Integer msgStatus;

    @Column(value = "msg_body")
    private String msgBody;

    @Column(value = "msg_body_type")
    private Integer msgBodyType;

    @Column(value = "msg_body_class")
    private String msgBodyClass;

    @Column(value = "msg_attribute")
    private String msgAttribute;

    @Column(value = "msg_type")
    private String msgType;

    @Column(value = "create_time")
    private Date createTime;

    @Column(value = "send_time")
    private Date sendTime;


    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
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

    public Integer getMsgStatus() {
        return msgStatus;
    }

    public void setMsgStatus(Integer msgStatus) {
        this.msgStatus = msgStatus;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
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

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getMsgBodyType() {
        return msgBodyType;
    }

    public void setMsgBodyType(Integer msgBodyType) {
        this.msgBodyType = msgBodyType;
    }

    public String getMsgAttribute() {
        return msgAttribute;
    }

    public void setMsgAttribute(String msgAttribute) {
        this.msgAttribute = msgAttribute;
    }

    public String getMsgBodyClass() {
        return msgBodyClass;
    }

    public void setMsgBodyClass(String msgBodyClass) {
        this.msgBodyClass = msgBodyClass;
    }
}
