package com.ziroom.framework.module.distributedlock;

import java.util.concurrent.locks.Lock;

/**
 * Created by liangrk on 2022/5/4.
 */
public interface LockGetter {

    Lock getLock(String name);

}
