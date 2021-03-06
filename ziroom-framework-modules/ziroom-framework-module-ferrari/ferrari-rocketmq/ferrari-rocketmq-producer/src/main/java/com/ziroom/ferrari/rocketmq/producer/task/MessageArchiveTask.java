package com.ziroom.ferrari.rocketmq.producer.task;

import com.ziroom.ferrari.common.lock.FerrariLock;
import com.ziroom.ferrari.repository.common.utils.FerrariDateUtils;
import com.ziroom.ferrari.rocketmq.producer.service.MessageArchiveService;
import com.ziroom.ferrari.repository.producer.FerrariMessageDao;
import com.ziroom.ferrari.repository.producer.entity.FerrariMessage;
import com.ziroom.ferrari.repository.common.FerrariMessageStatus;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangzonggi
 * @Date: 2022-04-29 10:28
 * @Version 1.0
 */
@Component("rocketMQMessageArchiveTask")
public class MessageArchiveTask extends AbstractFerrariTask implements InitializingBean {

    @Resource
    private FerrariMessageDao ferrariMessageDao;

    @Resource
    private MessageArchiveService archiveService;

    @Resource
    private FerrariLock ferrariLock;

    private static final String LOCK_KEY = "RocketMQMessageArchiveTask";

    @Override
    public void run() {
        doTask(FerrariMessageStatus.SUCCESS);
        doTask(FerrariMessageStatus.FAILED_NOT_RETRY);
    }

    private void doTask(FerrariMessageStatus ferrariMessageStatus) {
        List<FerrariMessage> ferrariMessageList = ferrariMessageDao.findBySendTime(ferrariMessageStatus.getCode(), FerrariDateUtils.minus(new Date(), 3, FerrariDateUtils.TimeUnit.DAYS));
        if (CollectionUtils.isEmpty(ferrariMessageList)) {
            return;
        }
        ferrariMessageList.forEach(f -> {
            try {
                archiveService.doArchive(f);
            } catch (Exception ignored) {
            }
        });
    }

    @Override
    protected void releaseLock() {
        ferrariLock.releaseLock(LOCK_KEY);
    }

    @Override
    protected boolean lock() {
        return ferrariLock.lock(LOCK_KEY, 15, TimeUnit.HOURS);
    }

    @Override
    public void afterPropertiesSet() {
        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay = getTimeMillis("06:30:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
        executorService.scheduleAtFixedRate(this::doTask, initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

    private long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
        }
        return 0;
    }
}
