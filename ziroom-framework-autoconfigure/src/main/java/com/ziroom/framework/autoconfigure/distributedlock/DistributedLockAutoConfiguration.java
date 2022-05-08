package com.ziroom.framework.autoconfigure.distributedlock;

import com.ziroom.framework.module.distributedlock.*;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.AutoProxyRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liangrk on 2022/5/4.
 */
@Configuration
@ConditionalOnClass(DistributedLock.class)
@Import({AutoProxyRegistrar.class})
@AutoConfigureAfter(name = "org.redisson.spring.starter.RedissonAutoConfiguration")
public class DistributedLockAutoConfiguration {



    @Bean
    @ConditionalOnProperty(name = "ziroom.framework.distributedLock.type", havingValue = "redisson", matchIfMissing = true)
    @ConditionalOnMissingBean(LockGetter.class)
    @ConditionalOnClass(name = "org.redisson.api.RedissonClient")
    @ConditionalOnBean(type ="org.redisson.api.RedissonClient")
    public LockGetter redissonLockGetter() {
        return new RedissonLockGetter();
    }

    /**
     * zk
     */
    @ConditionalOnProperty(name = "ziroom.framework.distributedLock.type", havingValue = "zookeeper", matchIfMissing = false)
    @Bean
    public LockGetter zookeeperLockGetter() {
        return new ZookeeperLockGetter();
    }


    @Bean
    @ConditionalOnBean(LockGetter.class)
    DistributedLockAspect DistributedLockAspect(LockGetter lockGetter) {
        return new DistributedLockAspect(lockGetter);
    }

}
