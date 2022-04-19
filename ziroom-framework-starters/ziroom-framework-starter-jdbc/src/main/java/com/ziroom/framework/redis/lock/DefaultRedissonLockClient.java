package com.ziroom.framework.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 描述:  红锁-实现
 *
 * @author xugw
 * @date 2022/1/17
 */
@Component
@Slf4j
public class DefaultRedissonLockClient implements LockClient {

    private static final String DEFAULT_MSG = "获取锁失败，请重试";

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void lock(String key, String exMsg) throws Exception{
        RLock lock = redissonClient.getLock(key);
        if (!lock.tryLock()) {
            throw new Exception();
        }
    }

    @Override
    public void lock(Runnable runnable, String key) throws Exception{
        lock(key, DEFAULT_MSG);
        try {
            runnable.run();
        } finally {
            unlock(key);
        }
    }

    @Override
    public void lock(String key) throws Exception{
        lock(key, DEFAULT_MSG);
    }

    @Override
    public void unlock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (!Objects.isNull(lock) && lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
