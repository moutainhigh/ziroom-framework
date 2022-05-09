package com.ziroom.ferrari.repository.core.transaction;

import java.util.HashSet;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 18:03
 * @Version 1.0
 */
public class TransactionContext {
    private TransactionContext() {
    }

    /**
     * 标识当前处于事物中的threadId
     */
    private static final HashSet<Long> inTransThreadIdSet = new HashSet();

    /**
     * ThreadLocal标识当前线程是否处于事物中
     */
    private static final ThreadLocal<Boolean> context = new ThreadLocal<Boolean>() {
        @Override
        public Boolean initialValue() {
            return Boolean.FALSE;
        }
    };

    public static void enterTransaction() {
        context.set(Boolean.TRUE);
        inTransThreadIdSet.add(Thread.currentThread().getId());
    }

    public static void leaveTransaction() {
        context.set(Boolean.FALSE);
        inTransThreadIdSet.remove(Thread.currentThread().getId());
    }

    public static boolean isInTransaction() {
        return context.get();
    }

    public static boolean isInTransaction(Thread thread) {
        if (Thread.currentThread() == thread) {
            return isInTransaction();
        }

        return inTransThreadIdSet.contains(thread.getId());
    }
}
