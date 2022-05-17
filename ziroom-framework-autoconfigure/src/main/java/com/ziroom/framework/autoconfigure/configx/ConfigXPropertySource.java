package com.ziroom.framework.autoconfigure.configx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ConfigXPropertySource extends MapPropertySource {
    private static final String SUFFIX_YAML = "yaml";

    private static final String SUFFIX_YML = "yml";
    private String pathPattern;
    private String propertyPrefix;

    public ConfigXPropertySource(String name, String pathPattern, String propertyPrefix) {
       super(name, new HashMap<>());
        this.pathPattern = pathPattern;
        this.propertyPrefix = propertyPrefix;
        if (!propertyPrefix.endsWith(".")) {
            this.propertyPrefix = propertyPrefix + ".";
        }
        this.init();
    }

    private void init() {
        try {
            // todo 支持yaml格式， 该模块单独抽离开， 设计成通用模块
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:" + pathPattern);

            for (int i = 0; i < resources.length; i++) {
                Resource resource = resources[i];
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

                int index = i;
                mapPropertySource.forEach((key, value) -> {
                    key = processKey(key, index);
                    value = processValue(value);
                    getSource().put(key, value);
                });

            }

        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }

    private Object processValue(Object value) {
        return value instanceof OriginTrackedValue ? ((OriginTrackedValue) value).getValue() : value;
    }

    private String processKey(String key, int i) {
        if (!key.startsWith(this.propertyPrefix)) {
            return key;
        }

        String suffix = key.substring(this.propertyPrefix.length());
        return String.format("%s[%d].%s", this.propertyPrefix.substring(0, this.propertyPrefix.length()), i, suffix);
    }

}
