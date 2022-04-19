package com.ziroom.framework.redis.lock;

/**
 * 锁收拢
 *
 * @author xugw
 * @version 0.1.0
 * @date 2022/1/17
 */
public interface LockClient {

    /**
     * 默认异常锁
     *
     * @param key key
     */
    void lock(String key) throws Exception;

    /**
     * 解锁
     *
     * @param key key
     */
    void unlock(String key);

    /**
     * 锁-带自定义异常
     *
     * @param key   key
     * @param exMsg ex
     */
    void lock(String key, String exMsg) throws Exception;

    /**
     * 锁-自动解锁
     *
     * @param runnable runnable
     * @param key      key
     */
    void lock(Runnable runnable, String key) throws Exception;


}
