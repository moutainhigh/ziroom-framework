package com.ziroom.framework.module.distributedlock;

import java.util.concurrent.locks.Lock;

/**
 * Created by liangrk on 2022/5/4.
 */
public interface DistributedLock extends Lock {

    /**
     * get lock name
     */
    String getName();

}
