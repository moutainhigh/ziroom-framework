package com.ziroom.framework.ferrari.repository.common.entity;

import com.ziroom.framework.ferrari.repository.core.annotation.Column;
import com.ziroom.framework.ferrari.repository.core.annotation.Table;
import com.ziroom.framework.ferrari.repository.core.entity.IdEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: J.T.
 * @Date: 2021/9/1 11:17
 * @Version 1.0
 */
@Table(value = "ferrari_lock")
public class FerrariLockEntity extends IdEntity implements Serializable {

    @Column(value = "unique_key")
    private String uniqueKey;

    @Column(value = "locked")
    private Integer locked;

    @Column(value = "expire_date")
    private Date expireDate;

    @Column(value = "version")
    private Integer version;


    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public Integer getLocked() {
        return locked;
    }

    public void setLocked(Integer locked) {
        this.locked = locked;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
