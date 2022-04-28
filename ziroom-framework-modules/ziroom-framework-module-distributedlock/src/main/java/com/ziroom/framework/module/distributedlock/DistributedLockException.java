package com.ziroom.framework.module.distributedlock;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常
 *
 * @author zhangkx1
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DistributedLockException extends RuntimeException {

    private final Integer code;
    private final String message;

    public DistributedLockException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public DistributedLockException(DistributedLockCode distributedLockCode) {
        super(distributedLockCode.getMessage());
        this.code = distributedLockCode.getCode();
        this.message = distributedLockCode.getMessage();
    }
}
