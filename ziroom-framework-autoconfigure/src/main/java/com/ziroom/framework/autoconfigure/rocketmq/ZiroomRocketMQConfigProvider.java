package com.ziroom.framework.autoconfigure.rocketmq;

import com.ziroom.framework.autoconfigure.jdbc.ZiroomDataSourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZiroomRocketMQConfigProvider {

    private static final Logger log = LoggerFactory.getLogger(ZiroomDataSourceProvider.class);

    public static final String ROCKETMQ_PREFIX = "ziroom.rocketmq.";

    private static final String SUFFIX_YAML = "yaml";

    private static final String SUFFIX_YML = "yml";

    private final Map<String, PropertySource<?>> configMap = new HashMap<>();

    protected void initialize() {
        try {
            // todo 支持yaml格式， 该模块单独抽离开， 设计成通用模块
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:rocketmq-*");
            for (Resource resource : resources) {
                //目前只支持yaml yml格式
                if (resource.getFilename() == null || (!resource.getFilename().endsWith(SUFFIX_YAML) &&
                    !resource.getFilename().endsWith(SUFFIX_YML))) {
                    continue;
                }
                List<PropertySource<?>> ziroomPropertySources = new YamlPropertySourceLoader().load(resource.getFilename(), resource);
                if (ziroomPropertySources == null || ziroomPropertySources.size() == 0) {
                    continue;
                }

                PropertySource<?> ziroomPropertySource = ziroomPropertySources.get(0);
                Map<String, Object> mapPropertySource = (Map<String, Object>) ziroomPropertySource.getSource();

                Object nameServer = mapPropertySource.get(ROCKETMQ_PREFIX + "name-server");
                if (StringUtils.isEmpty(nameServer)) {
                    log.warn("无效 RocketMQ 配置[rocketmq.name-server 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }

                Object name = mapPropertySource.get(ROCKETMQ_PREFIX + "name");
                String sourceName = String.valueOf(name);
                if (StringUtils.isEmpty(name)) {
                    String fileName = resource.getFilename();
                    String dataName = fileName.substring(fileName.indexOf("rocketmq-") + "rocketmq-".length(), fileName.lastIndexOf("."));
                    if (StringUtils.isEmpty(dataName)) {
                        log.warn("无效 RocketMQ 配置[rocketmq.name 为空]，请检查配置文件：" + resource.getFilename());
                        continue;
                    }
                    sourceName = dataName;
                }

                log.info("注册 RocketMQ [{}]. 加载自: {}", nameServer, resource.getFilename());
                this.configMap.put(sourceName, ziroomPropertySource);
            }

        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }

    public Map<String, PropertySource<?>> getConfigMap() {
        return this.configMap;
    }

}
