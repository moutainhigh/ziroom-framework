package common.lock;

/**
 * @Author: J.T.
 * @Date: 2021/9/1 15:04
 * @Version 1.0
 */
public enum LockEnum {

    LOCK(1),
    UNLOCK(2);

    private int code;

    LockEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
