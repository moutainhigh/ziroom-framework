package com.ziroom.ferrari.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

public class MessageTest {
    @Test
    public void test() throws JsonProcessingException {
        String json = "{\"payload\":{\"uid\":\"1791a0ff-589c-46fd-a527-0109991600a1\",\"rentContractCode\":\"test\",\"moduleType\":\"broadband\",\"appId\":null},\"headers\":{\"topic\":\"mycenter-common-notice-topic\",\"id\":\"164e5178-6191-f78c-e5fc-8a296f452b2e\",\"tags\":\"tag\",\"timestamp\":1651232484833}}";
        ObjectMapper om = new ObjectMapper();
        Message message = om.readValue(json, GenericMessage.class);
        System.out.println(message);
    }
}
