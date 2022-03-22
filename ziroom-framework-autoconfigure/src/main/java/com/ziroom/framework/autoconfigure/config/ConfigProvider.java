package com.ziroom.framework.autoconfigure.config;


import com.alibaba.nacos.api.exception.NacosException;

/**
 * @Description
 * @Author lidm
 * @Date 2020/11/5
 */
public class ConfigProvider implements Provider<ConfigManager> {
    NacosConfigManager nacosConfigManager;

    @Override
    public void initialize() {
        init();
    }


    private void init() {
        if (nacosConfigManager == null){
            nacosConfigManager = new NacosConfigManager();
            try {
                nacosConfigManager.initialize();
            } catch (NacosException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public ConfigManager get() {
        return nacosConfigManager;
    }
}
