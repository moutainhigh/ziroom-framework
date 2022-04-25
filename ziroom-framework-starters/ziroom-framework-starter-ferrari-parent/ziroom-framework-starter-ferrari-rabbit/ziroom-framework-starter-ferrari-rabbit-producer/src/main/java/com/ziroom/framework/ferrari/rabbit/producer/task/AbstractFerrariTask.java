package com.ziroom.framework.ferrari.rabbit.producer.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @Author: J.T.
 * @Date: 2021/9/1 16:55
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
