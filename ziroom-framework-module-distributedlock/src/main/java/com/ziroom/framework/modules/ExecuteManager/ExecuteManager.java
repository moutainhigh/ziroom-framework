
package com.ziroom.framework.modules.ExecuteManager;

/**
 * 执行管理器
 *
 * @author xugw
 * @version 0.1.0
 * @date 2022/1/17
 */
public interface ExecuteManager {

    /**
     * 事务执行
     * @param runnable 运行参数
     */
    void transactionExe(Runnable runnable);

    /**
     * 异步执行
     * @param runnable rn
     */
    void asyncExecute(Runnable runnable);

}
