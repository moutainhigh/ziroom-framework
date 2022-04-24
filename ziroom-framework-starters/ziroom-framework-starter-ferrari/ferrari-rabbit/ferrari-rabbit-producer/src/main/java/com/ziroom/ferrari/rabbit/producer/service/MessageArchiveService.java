package com.ziroom.ferrari.rabbit.producer.service;

import com.ziroom.ferrari.repository.core.util.BeanUtils;
import com.ziroom.ferrari.repository.producer.FerrariMessageArchiveDao;
import com.ziroom.ferrari.repository.producer.FerrariMessageDao;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessageArchive;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @Author: J.T.
 * @Date: 2021/9/1 17:35
 * @Version 1.0
 */
@Service
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
