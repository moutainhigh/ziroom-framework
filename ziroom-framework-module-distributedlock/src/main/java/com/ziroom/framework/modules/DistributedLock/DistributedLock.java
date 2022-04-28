
package com.ziroom.framework.modules.distributedLock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 下面的例子会把account取出来当做锁的名称
 *
 * @author zhangkx1
 * @DistributedLock(lockName = "user.account")
 * public void update(User user)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 分布式锁名称
     */
    String lockName();

}
