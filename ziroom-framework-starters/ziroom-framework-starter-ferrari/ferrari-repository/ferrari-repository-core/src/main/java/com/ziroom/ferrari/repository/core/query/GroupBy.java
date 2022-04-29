package com.ziroom.ferrari.repository.core.query;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:17
 * @Version 1.0
 */
public class GroupBy {

    //分组属性
    private String key;
    //如需要每组数量,那么该字段指定每组数量的别名，相当于select count(*) as group_count，entity需要指定一个非持久化的字段和该别名保持一致
    private String groupCountAlias;

    private GroupBy(String key, String groupCountAlias) {
        this.key = key;
        this.groupCountAlias = groupCountAlias;
    }

    /**
     * Static factory method to create an Update
     *
     * @return
     */
    public static GroupBy groupBy(String key) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "Param key was %s, It must be not null or empty", key);
        return new GroupBy(key, null);
    }

    public static GroupBy groupBy(String key, String groupCountAlias) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(key), "Param key was %s, It must be not null or empty", key);
        return new GroupBy(key, groupCountAlias);
    }

    public String getKey() {
        return key;
    }

    public String getGroupCountAlias() {
        return groupCountAlias;
    }
}
