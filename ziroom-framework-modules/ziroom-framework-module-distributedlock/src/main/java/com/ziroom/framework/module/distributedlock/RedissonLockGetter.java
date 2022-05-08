package com.ziroom.framework.module.distributedlock;

import javax.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

/**
 * Created by liangrk on 2022/5/4.
 */
public class RedissonLockGetter implements LockGetter{

    @Resource
    RedissonClient redisson;

    @Override
    public DistributedLock getLock(String name) {
        RLock lock = redisson.getLock(name);
        return new DefaultDistributedLock(lock, name);
    }
}
