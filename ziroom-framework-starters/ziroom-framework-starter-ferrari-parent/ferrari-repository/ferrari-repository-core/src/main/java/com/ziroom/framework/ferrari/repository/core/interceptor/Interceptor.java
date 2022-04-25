package com.ziroom.framework.ferrari.repository.core.interceptor;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 18:06
 * @Version 1.0
 */
public interface Interceptor {

    /**
     * 处理一次拦截
     *
     * @param invocation
     * @return - 返回目标方法的返回值
     * @throws Throwable
     */
    Object intercept(Invocation invocation) throws Throwable;

    /**
     * 注册一个拦截
     *
     * @param target
     * @return - 返回的是代理对象或者目标对象本身
     */
    Object plugin(Object target);
}

