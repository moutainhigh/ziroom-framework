
package com.ziroom.framework.module.distributedlock;

import lombok.Getter;

/**
 * <p>
 * 业务状态码
 * </p>
 *
 * @author zhangkx1
 * @date Created in 2018-12-07 14:31
 */
@Getter
public enum DistributedLockCode{


    /**
     * 加锁失败
     */
    TRY_LOCK_FAIL(5002, "加锁失败");
    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 返回信息
     */
    private final String message;

    DistributedLockCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return String.format(" Status:{code=%s, message=%s} ", getCode(), getMessage());
    }

}
