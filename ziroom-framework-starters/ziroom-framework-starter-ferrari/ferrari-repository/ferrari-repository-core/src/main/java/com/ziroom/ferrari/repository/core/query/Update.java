package com.ziroom.ferrari.repository.core.query;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:20
 * @Version 1.0
 */
public class Update {

    /**
     * set操作集合,大部分情况一次更新字典不会超过4个,默认Map初始容量16
     */
    private Map<String, Object> setMap;

    public Update() {
        setMap = Maps.newLinkedHashMapWithExpectedSize(4);
    }

    /**
     * Static factory method to create an Update
     *
     * @return
     */
    public static Update update(String key, Object value) {
        Update update = new Update();
        update.set(key, value);
        return update;
    }

    /**
     * update中的set操作
     *
     * @param key   - 不可为null 或 empty
     * @param value - 可为null
     * @return
     */
    public Update set(String key, Object value) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "Param key was %s, It must be not null or empty", key);
        Preconditions.checkArgument(!setMap.containsKey(key), "Param key was %s, It has been seted", key);
        setMap.put(key, value);
        return this;
    }

    /**
     * update中的get操作
     *
     * @param key - 不可为null 或 empty
     * @return
     */
    public Object get(String key) {
        return setMap.get(key);
    }

    /**
     * @return the setMap
     */
    public Map<String, Object> getSetMap() {
        return setMap;
    }

    @Override
    public String toString() {
        return this.setMap.toString();
    }
}
