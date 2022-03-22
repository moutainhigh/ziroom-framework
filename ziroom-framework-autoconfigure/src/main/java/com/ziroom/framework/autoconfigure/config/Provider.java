package com.ziroom.framework.autoconfigure.config;

/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
public interface Provider<T> {
    void initialize();
    T get();
}
