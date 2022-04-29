package com.ziroom.ferrari.test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ziroom.ferrari.rocketmq.producer.FerrariRocketmqTemplate;
import com.ziroom.ferrari.test.dao.ContractLabelMapper;
import com.ziroom.ferrari.test.dto.ModuleTypeEnum;
import com.ziroom.ferrari.test.dto.NoticeReq;
import com.ziroom.ferrari.test.entity.ContractLabelEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:14
 * @Version 1.0
 */
@Service
public class TestRocketmqService {

    @Resource
    private ContractLabelMapper contractLabelMapper;

    @Resource
    private FerrariRocketmqTemplate ferrariRocketmqTemplate;

    public static final String NOTICE_TOPIC = "mycenter-common-notice-topic";
    public static final String CONSUMER_GROUP_LISTENTER = "ferrari_test_listener";
    public static final String CONSUMER_GROUP_BEAN = "ferrari_test_bean";

    @Transactional(rollbackFor = Exception.class)
    public void test() throws JsonProcessingException {
        ContractLabelEntity labelEntity = newContractLabel();
        contractLabelMapper.insertSelective(labelEntity);

        NoticeReq noticeReq = NoticeReq.builder()
                .moduleType(ModuleTypeEnum.broadband)
                .rentContractCode("test")
                .uid("1791a0ff-589c-46fd-a527-0109991600a1")
                .build();

        ferrariRocketmqTemplate.syncSend(NOTICE_TOPIC, "tag", noticeReq);
    }

//
//    @Transactional(rollbackFor = Exception.class)
//    public void testWithException() {
//        ContractLabelEntity labelEntity = newContractLabel();
//        contractLabelMapper.insertSelective(labelEntity);
//        ferrariRabbitTemplate.convertAndSend("crm.e.hire.newsign", "hire.newsign.saveHireContractComplete", labelEntity);
//        throw new RuntimeException();
//    }


//    public void test2() {
//        ContractLabelEntity labelEntity = newContractLabel();
//        contractLabelMapper.insertSelective(labelEntity);
//        ferrariRabbitTemplate.convertAndSend("crm.e.hire.newsign", "hire.newsign.saveHireContractComplete", labelEntity);
//    }

    private ContractLabelEntity newContractLabel() {
        return ContractLabelEntity.builder()
                .hireContractId(12356532L)
                .versionCode("test")
                .build();
    }

//    public void testWithException2() {
//        ContractLabelEntity labelEntity = newContractLabel();
//        contractLabelMapper.insertSelective(labelEntity);
//        ferrariRabbitTemplate.convertAndSend("crm.e.hire.newsign", "hire.newsign.saveHireContractComplete", labelEntity);
//        throw new RuntimeException();
//    }
}
