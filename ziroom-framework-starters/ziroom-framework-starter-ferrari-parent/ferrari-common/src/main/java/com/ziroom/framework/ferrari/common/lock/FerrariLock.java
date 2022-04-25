package com.ziroom.framework.ferrari.common.lock;

import java.util.concurrent.TimeUnit;

/**
 * @Author: J.T.
 * @Date: 2021/9/1 11:28
 * @Version 1.0
 */
public interface FerrariLock {

    /**
     * 默认 1小时
     *
     * @author : J.T.
     * @date : 2021/9/1 11:30
     **/
    boolean lock(String lockKey);

    boolean lock(String lockKey, int lockTime, TimeUnit timeUnit);

    boolean releaseLock(String lockKey);
}
