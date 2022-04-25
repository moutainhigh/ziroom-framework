package com.ziroom.framework.ferrari.repository.core.entity;

import com.ziroom.framework.ferrari.repository.core.annotation.Column;

import java.io.Serializable;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:06
 * @Version 1.0
 */
public abstract class IdEntity implements Serializable {

    @Column(value = "id")
    protected long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

