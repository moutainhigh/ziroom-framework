package com.ziroom.framework.autoconfigure.cloud.discovery.zns;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryAutoConfiguration;
import com.ziroom.framework.autoconfigure.utils.OmegaEnvLevel;
import com.ziroom.framework.autoconfigure.utils.OmegaUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@AutoConfigureBefore(value = NacosDiscoveryAutoConfiguration.class)
public class ZnsDiscoveryAutoConfiguration implements EnvironmentAware {

    public static final String PRODUCTION_ZNS_ENDPOINT = "http://zns.kp.ziroom.com";
    public static final String NON_PRODUCTION_ZNS_ENDPOINT = "http://zns.kt.ziroom.com";

    public static final String DEFAULT_NAMESPACE = "daily.ziroom.svc";

    private Environment environment;

    @Bean
    @ConfigurationProperties(prefix = "spring.cloud.discovery.zns")
    public NacosDiscoveryProperties znsProperties() {
        NacosDiscoveryProperties properties = new NacosDiscoveryProperties();

        // 根据环境识别 zns 地址
        properties.setServerAddr(getZnsServerAddress());

        // 根据环境识别 namespace
        String namespace;
        OmegaEnvLevel envLevel = OmegaUtils.getRuntimeEnvLevel(environment);
        if (envLevel == OmegaEnvLevel.LOCAL) {
            namespace = DEFAULT_NAMESPACE;
        } else {
            namespace = String.format("%s.ziroom.svc", envLevel.getEnvValue());
        }
        properties.setNamespace(namespace);

        return properties;
    }


    public String getZnsServerAddress() {
        OmegaEnvLevel envLevel = OmegaUtils.getRuntimeEnvLevel(environment);
        if (envLevel == OmegaEnvLevel.PRODUCTION) {
            return PRODUCTION_ZNS_ENDPOINT;
        }

        return NON_PRODUCTION_ZNS_ENDPOINT;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
