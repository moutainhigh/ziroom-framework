package com.ziroom.framework.autoconfigure.jdbc;

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

import static com.ziroom.framework.autoconfigure.jdbc.PropertyConstants.*;


/**
 * 自如数据库配置读取
 * @author zhaoy13,liangrk,kanggh
 */
public class ZiroomDataSourceProvider {

    private static final Logger log = LoggerFactory.getLogger(ZiroomDataSourceProvider.class);

    public static final String DATASOURCE_PREFIX = "ziroom.datasource.";

    private static final String DATASOURCE_SUFFIX_YAML = "yaml";

    private static final String DATASOURCE_SUFFIX_YML = "yml";

    private Map<String, PropertySource<?>> ziroomDataSourceMap = new HashMap<>();

    protected void initialize() {
        try {
            // todo 支持yaml格式， 该模块单独抽离开， 设计成通用模块
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:datasource-*");
            for (Resource resource : resources) {
                //目前只支持yaml yml格式
                if (resource.getFilename() == null || (!resource.getFilename().endsWith(DATASOURCE_SUFFIX_YAML) &&
                        !resource.getFilename().endsWith(DATASOURCE_SUFFIX_YML))) {
                    continue;
                }
                List<PropertySource<?>> ziroomPropertySources =  new YamlPropertySourceLoader().load(resource.getFilename(),resource);
                if (ziroomPropertySources == null || ziroomPropertySources.size() ==0){
                    continue;
                }
                PropertySource<?> ziroomPropertySource = ziroomPropertySources.get(0);
                Binder binder = new Binder(ConfigurationPropertySources.from(ziroomPropertySources),
						new PropertySourcesPlaceholdersResolver(ziroomPropertySources));

                String userName = binder.bind(DATASOURCE_PREFIX + DATA_USERNAME, Bindable.of(String.class)).get();
                if (CommonMixUtils.isBlank(userName)) {
                    log.warn("无效数据库配置[datasource.username 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }
                String url = binder.bind(DATASOURCE_PREFIX + DATA_URL, Bindable.of(String.class)).get();
                if (CommonMixUtils.isBlank(url)) {
                    log.warn("无效数据库配置[datasource.url 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }
                String driverClassName = binder.bind(DATASOURCE_PREFIX + DATA_DRIVER_CLASS_NAME, Bindable.of(String.class)).get();
                if (StringUtils.isEmpty(driverClassName)) {
                    log.info("注册数据源[{}]. 加载自: {} driver-class-name设置为空,使用默认值：com.mysql.cj.jdbc.Driver", userName, resource.getFilename());
                }
                String type = binder.bind(DATASOURCE_PREFIX + DATA_TYPE, Bindable.of(String.class)).get();
                if (StringUtils.isEmpty(type)) {
                    log.info("注册数据源[{}]. 加载自: {} type设置为空,使用默认值：com.zaxxer.hikari.HikariDataSource", userName, resource.getFilename());
                }
                String name = binder.bind(DATASOURCE_PREFIX + DATA_NAME, Bindable.of(String.class)).get();
                log.info("注册数据源[{}][{}]. 加载自: {}", userName, url, resource.getFilename());
                this.ziroomDataSourceMap.put(name, ziroomPropertySource);
            }
        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }

    public Map<String, PropertySource<?>> getZiroomDataSourceMap() {
        return this.ziroomDataSourceMap;
    }

    public static void main(String[] args) {
        new ZiroomDataSourceProvider().initialize();
    }
}
