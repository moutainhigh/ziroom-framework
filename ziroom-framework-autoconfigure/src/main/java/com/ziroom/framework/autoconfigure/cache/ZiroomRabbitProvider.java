package com.ziroom.framework.autoconfigure.cache;

import com.ziroom.framework.autoconfigure.common.CommonMixUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ziroom.framework.autoconfigure.jdbc.PropertyConstants.DATA_NAME;


/**
 * 自如数据库配置读取
 * @author zhaoy13,liangrk,kanggh
 */
public class ZiroomRabbitProvider {

    private static final Logger log = LoggerFactory.getLogger(ZiroomRabbitProvider.class);

    public static final String RABBIT_PREFIX = "ziroom.rabbitmq.";

    private static final String SUFFIX_YAML = "yaml";

    private static final String SUFFIX_YML = "yml";

    private Map<String, PropertySource<?>> ziroomRabbitMap = new HashMap<>();

    protected void initialize() {
        try {
            // todo 支持yaml格式， 该模块单独抽离开， 设计成通用模块
            Resource[] rabbitResources = new PathMatchingResourcePatternResolver().getResources("classpath:rabbitmq-*");
            for (Resource resource : rabbitResources) {
                //目前只支持yaml yml格式
                if (resource.getFilename() == null || (!resource.getFilename().endsWith(SUFFIX_YAML) &&
                        !resource.getFilename().endsWith(SUFFIX_YML))) {
                    continue;
                }
                List<PropertySource<?>> ziroomPropertySources =  new YamlPropertySourceLoader().load(resource.getFilename(),resource);
                if (ziroomPropertySources == null || ziroomPropertySources.size() ==0){
                    continue;
                }
                PropertySource<?> ziroomPropertySource = ziroomPropertySources.get(0);
                Binder binder = new Binder(ConfigurationPropertySources.from(ziroomPropertySources),
						new PropertySourcesPlaceholdersResolver(ziroomPropertySources));

                String host = binder.bind(RABBIT_PREFIX + RabbitPropertyConstants.HOST, Bindable.of(String.class)).get();
                if (CommonMixUtils.isBlank(host)) {
                    log.warn("无效rabbitmq配置[rabbitmq.host 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }
                String userName = binder.bind(RABBIT_PREFIX + RabbitPropertyConstants.USERNAME, Bindable.of(String.class)).get();
                if (CommonMixUtils.isBlank(userName)) {
                    log.warn("无效rabbitmq配置[rabbitmq.username 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }
                String virtualHost = binder.bind(RABBIT_PREFIX + RabbitPropertyConstants.VIRTUALHOST, Bindable.of(String.class)).get();
                if (StringUtils.isEmpty(virtualHost)) {
                    log.warn("无效rabbitmq配置[rabbitmq.virtualHost 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }
                String name = binder.bind(RABBIT_PREFIX + DATA_NAME, Bindable.of(String.class)).get();
                log.info("注册rabbitmq[{}][{}][{}]. 加载自: {}", host, userName,virtualHost, resource.getFilename());
                this.ziroomRabbitMap.put(name, ziroomPropertySource);
            }
        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }

    public Map<String, PropertySource<?>> getZiroomRabbitMap() {
        return this.ziroomRabbitMap;
    }

    public static void main(String[] args) {
        new ZiroomRabbitProvider().initialize();
    }
}
