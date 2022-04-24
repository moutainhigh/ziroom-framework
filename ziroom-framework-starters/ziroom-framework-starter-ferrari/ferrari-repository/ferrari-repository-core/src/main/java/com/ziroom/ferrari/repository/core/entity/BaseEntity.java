package com.ziroom.ferrari.repository.core.entity;

import com.ziroom.ferrari.repository.core.annotation.Column;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:09
 * @Version 1.0
 */
public abstract class BaseEntity extends IdEntity {
    /**
     * 创建人标识可以存储UserId或UserCode
     */
    @Column("create_code")
    protected String createCode;
    /**
     * 创建时间
     */
    @Column("create_time")
    protected long createTime;
    /**
     * 最后修改人标识可以存储UserId或UserCode
     */
    @Column("last_modify_code")
    protected String lastModifyCode;
    /**
     * 最后修改时间
     */
    @Column("last_modify_time")
    protected long lastModifyTime;

    public String getCreateCode() {
        return createCode;
    }

    public void setCreateCode(String createCode) {
        this.createCode = createCode;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getLastModifyCode() {
        return lastModifyCode;
    }

    public void setLastModifyCode(String lastModifyCode) {
        this.lastModifyCode = lastModifyCode;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}

