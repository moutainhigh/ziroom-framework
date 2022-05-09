package com.ziroom.ferrari.repository.core.entity;

import com.ziroom.ferrari.repository.core.annotation.Column;

import java.io.Serializable;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:39
 * @Version 1.0
 */
public abstract class StringIdEntity implements Serializable {
    /**
     * 实体数据库主键 不具有任何业务含义<br>
     * 任何实体都不应该拿此作为关联 任何实体都应该找一个具有业务含义的唯一键( 用作实体之间关联)，且尽量用数字类型.<br>
     * 如果实在没有合适的，可以单独新增一个Long的字段，且命名用实体类名加Id命名,值可以和该实体的数据库主键一致
     */
    @Column("id")
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public abstract String toString();
}
