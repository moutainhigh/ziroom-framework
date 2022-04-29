package com.ziroom.ferrari.rocketmq.producer.service;

import com.ziroom.ferrari.repository.core.util.BeanUtils;
import com.ziroom.ferrari.repository.producer.FerrariMessageArchiveDao;
import com.ziroom.ferrari.repository.producer.FerrariMessageDao;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessageArchive;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author: zhangzonggi
 * @Date: 2022-04-29 10:28
 * @Version 1.0
 */
@Service("rocketMqMessageArchiveService")
public class MessageArchiveService {
    @Resource
    private FerrariMessageDao ferrariMessageDao;

    @Resource
    private FerrariMessageArchiveDao ferrariMessageArchiveDao;

    @Transactional(rollbackFor = Exception.class)
    public void doArchive(FerrariMessage ferrariMessage) {
        FerrariMessageArchive messageArchive = new FerrariMessageArchive();
        BeanUtils.copyProperties(messageArchive, ferrariMessage);
        ferrariMessageArchiveDao.insert(messageArchive);
        ferrariMessageDao.deleteById(ferrariMessage.getId(), "system");
    }
}
