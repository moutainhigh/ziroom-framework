package com.ziroom.framework.ferrari.common.lock;

import com.ziroom.framework.ferrari.repository.common.FerrariLockDao;
import com.ziroom.framework.ferrari.repository.common.entity.FerrariLockEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: J.T.
 * @Date: 2021/9/1 11:31
 * @Version 1.0
 */
@Primary
@Component("ferrariDatabaseLock")
public class FerrariDatabaseLock implements FerrariLock {

    @Resource
    private FerrariLockDao ferrariLockDao;

    @Override
    public boolean lock(String lockKey) {
        return lock(lockKey, 1, TimeUnit.HOURS);
    }

    @Override
    public boolean lock(String lockKey, int lockTime, TimeUnit timeUnit) {
        try {
            FerrariLockEntity entity = ferrariLockDao.findByUniqueKey(lockKey);
            if (isLock(entity)) {
                return false;
            }
            if (entity == null) {
                entity = new FerrariLockEntity();
                entity.setLocked(LockEnum.LOCK.getCode());
                entity.setExpireDate(new Date(System.currentTimeMillis() + timeUnit.toMillis(lockTime)));
                entity.setVersion(1);
                entity.setUniqueKey(lockKey);
                return ferrariLockDao.insert(entity) == 1;
            } else {
                entity.setLocked(LockEnum.LOCK.getCode());
                entity.setExpireDate(new Date(System.currentTimeMillis() + timeUnit.toMillis(lockTime)));
                return ferrariLockDao.updateByVersion(entity) == 1;
            }
        } catch (Exception ignored) {
            return false;
        }
    }

    @Override
    public boolean releaseLock(String lockKey) {
        try {
            FerrariLockEntity entity = ferrariLockDao.findByUniqueKey(lockKey);
            if (entity == null) {
                return true;
            }
            entity.setLocked(LockEnum.UNLOCK.getCode());
            return ferrariLockDao.updateByVersion(entity) == 1;
        } catch (Exception ignored) {
            return true;
        }
    }


    private boolean isLock(FerrariLockEntity entity) {
        return entity != null
                && entity.getLocked() == LockEnum.LOCK.getCode()
                && entity.getExpireDate().getTime() >= System.currentTimeMillis();
    }
}
