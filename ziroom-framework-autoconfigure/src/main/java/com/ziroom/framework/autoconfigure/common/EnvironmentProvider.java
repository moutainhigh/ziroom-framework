package com.ziroom.framework.autoconfigure.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;


/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
@Configuration
public class EnvironmentProvider implements ApplicationContextInitializer {

    private static final Log log = LogFactory.getLog(EnvironmentProvider.class);

    private static final String DEFAULT_ENV = "daily";

    protected String env;

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        // 1. 从环境变量加载
        String env = System.getenv("APPLICATION_ENV");
        if (CommonMixUtils.isNotBlank(env)) {
            // 2. 从 启动命令 参数读取
            env=System.getProperty("APPLICATION_ENV");
        }
        if (CommonMixUtils.isNotBlank(env)) {
            // 3. 从 ENV 参数读取
            env=System.getProperty("ENV");
        }
        if (CommonMixUtils.isNotBlank(env)) {
            log.info(String.format("read env from environment failed. use default environment:{}", DEFAULT_ENV));
            env = DEFAULT_ENV;
        }
    }
    public String getEnv(){
        return env;
    }
}
