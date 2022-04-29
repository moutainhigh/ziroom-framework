package com.ziroom.ferrari.rocketmq.producer.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author: zhangzonggi
 * @Date: 2022-04-29 10:28
 * @Version 1.0
 */
public abstract class AbstractFerrariTask {

    protected ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public void doTask() {
        if (!lock()) {
            return;
        }
        try {
            run();
        } finally {
            releaseLock();
        }
    }

    protected abstract void releaseLock();

    protected abstract boolean lock();

    public abstract void run();

}
