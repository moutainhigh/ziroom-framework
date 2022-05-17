package com.ziroom.ferrari.rocketmq.producer;

import org.apache.rocketmq.client.producer.SendResult;

public interface BatchSendCallback {
    void onSuccess(Object message, final SendResult sendResult);

    void onException(Object message, final Throwable e);
}
