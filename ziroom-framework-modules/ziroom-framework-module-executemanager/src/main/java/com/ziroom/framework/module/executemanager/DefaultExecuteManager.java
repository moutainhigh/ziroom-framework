package com.ziroom.framework.module.executemanager;

import cn.hutool.core.lang.Assert;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 默认执行器
 *
 * @author xugw
 * @version 0.1.0
 * @date 2022/1/17
 */
@Component
public class DefaultExecuteManager implements ExecuteManager {


    /**
     * 若不存在事务管理器，则无法使用事务
     * @param runnable 运行参数
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void transactionExe(Runnable runnable) {
        Assert.notNull(runnable, "runnable不能为null！");
        runnable.run();
    }

    @Override
    @Async
    public void asyncExecute(Runnable runnable) {
        Assert.notNull(runnable, "async-runnable不能为null！");
        runnable.run();
    }
}
