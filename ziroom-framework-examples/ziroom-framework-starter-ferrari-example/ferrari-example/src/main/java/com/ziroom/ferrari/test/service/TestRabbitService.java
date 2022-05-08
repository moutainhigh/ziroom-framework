package com.ziroom.ferrari.test.service;

import com.ziroom.ferrari.rabbit.producer.FerrariRabbitTemplate;
import com.ziroom.ferrari.test.dao.ContractLabelMapper;
import com.ziroom.ferrari.test.entity.ContractLabelEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author: J.T.
 * @Date: 2021/8/30 16:14
 * @Version 1.0
 */
@Service
public class TestRabbitService {

    @Resource
    private ContractLabelMapper contractLabelMapper;

    @Resource
    private FerrariRabbitTemplate ferrariRabbitTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void test() {
        ContractLabelEntity labelEntity = newContractLabel();
        contractLabelMapper.insertSelective(labelEntity);
        ferrariRabbitTemplate.convertAndSend("crm.e.hire.newsign", "hire.newsign.saveHireContractComplete", labelEntity);
    }


    @Transactional(rollbackFor = Exception.class)
    public void testWithException() {
        ContractLabelEntity labelEntity = newContractLabel();
        contractLabelMapper.insertSelective(labelEntity);
        ferrariRabbitTemplate.convertAndSend("crm.e.hire.newsign", "hire.newsign.saveHireContractComplete", labelEntity);
        throw new RuntimeException();
    }


    public void test2() {
        ContractLabelEntity labelEntity = newContractLabel();
        contractLabelMapper.insertSelective(labelEntity);
        ferrariRabbitTemplate.convertAndSend("crm.e.hire.newsign", "hire.newsign.saveHireContractComplete", labelEntity);
    }

    private ContractLabelEntity newContractLabel() {
        return ContractLabelEntity.builder()
                .hireContractId(12356532L)
                .versionCode("test")
                .build();
    }

    public void testWithException2() {
        ContractLabelEntity labelEntity = newContractLabel();
        contractLabelMapper.insertSelective(labelEntity);
        ferrariRabbitTemplate.convertAndSend("crm.e.hire.newsign", "hire.newsign.saveHireContractComplete", labelEntity);
        throw new RuntimeException();
    }
}
