package com.ziroom.framework.module.executemanager;

import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurationSelector;

/**
 * Created by liangrk on 2022/4/28.
 */
@Import({DefaultExecuteManager.class, AsyncConfigurationSelector.class})
public @interface EnableExecuteManager {

}
