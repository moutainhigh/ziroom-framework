package com.ziroom.framework.ferrari.repository.core.interceptor;

import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 18:06
 * @Version 1.0
 */
public class InterceptorChain {
    private Set<Interceptor> interceptors = Sets.newLinkedHashSet();
    private final Lock locker = new ReentrantLock();

    public Object pluginAll(Object target) {
        for (Interceptor interceptor : interceptors) {
            target = interceptor.plugin(target);
        }
        return target;
    }

    public void addInterceptor(Interceptor interceptor) {
        locker.lock();
        try {
            interceptors.add(interceptor);
        } finally {
            locker.unlock();
        }
    }

    public void setInterceptors(List<Interceptor> interceptors) {
        if (interceptors == null || interceptors.isEmpty()) {
            return;
        }
        for (Interceptor interceptor : interceptors) {
            locker.lock();
            try {
                this.interceptors.add(interceptor);
            } finally {
                locker.unlock();
            }
        }
    }
}
