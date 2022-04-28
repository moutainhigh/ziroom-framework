package com.ziroom.framework.modules.executemanager;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
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
