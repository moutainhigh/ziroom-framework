package com.ziroom.framework.module.distributedlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Created by liangrk on 2022/5/4.
 */
public class DefaultDistributedLock implements DistributedLock {

    Lock delegate;

    String lockName;

    public DefaultDistributedLock(Lock lock, String lockName) {
        this.delegate = lock;
        this.lockName = lockName;
    }

    @Override
    public void lock() {
        delegate.lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        delegate.lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        return delegate.tryLock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return delegate.tryLock(time, unit);
    }

    @Override
    public void unlock() {
        delegate.unlock();
    }

    @Override
    public Condition newCondition() {
        return delegate.newCondition();
    }

    @Override
    public String getName() {
        return lockName;
    }
}
