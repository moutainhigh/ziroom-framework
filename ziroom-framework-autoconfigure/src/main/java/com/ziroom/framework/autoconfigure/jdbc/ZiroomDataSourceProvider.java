package com.ziroom.framework.autoconfigure.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.util.Collections;
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
                Map<String, Object> mapPropertySource = (Map<String, Object>)ziroomPropertySource.getSource();

                Object userName = mapPropertySource.get(DATASOURCE_PREFIX + DATA_USERNAME);
                if (StringUtils.isEmpty(userName)) {
                    log.warn("无效数据库配置[datasource.username 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }
                Object url = mapPropertySource.get(DATASOURCE_PREFIX + DATA_URL);
                if (StringUtils.isEmpty(url)) {
                    log.warn("无效数据库配置[datasource.url 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }
                Object name = mapPropertySource.get(DATASOURCE_PREFIX + DATA_NAME);
                String sourceName = String.valueOf(name);
                if (StringUtils.isEmpty(name)) {
                    String fileName = resource.getFilename();
                    String dataName = fileName.substring(fileName.indexOf("datasource-")+"datasource-".length(),fileName.lastIndexOf("."));
                    if (StringUtils.isEmpty(dataName)) {
                        log.warn("无效数据库配置[datasource.name 为空]，请检查配置文件：" + resource.getFilename());
                        continue;
                    }
                    sourceName = dataName;
                }
                Object driverClassName = mapPropertySource.get(DATASOURCE_PREFIX + DATA_DRIVER_CLASS_NAME);
                if (StringUtils.isEmpty(driverClassName)) {
                    log.info("注册数据源[{}]. 加载自: {} driver-class-name设置为空,使用默认值：com.mysql.cj.jdbc.Driver", userName, resource.getFilename());
                }
                Object type = mapPropertySource.get(DATASOURCE_PREFIX + DATA_TYPE);
                if (StringUtils.isEmpty(type)) {
                    log.info("注册数据源[{}]. 加载自: {} type设置为空,使用默认值：com.zaxxer.hikari.HikariDataSource", userName, resource.getFilename());
                }
                log.info("注册数据源[{}][{}]. 加载自: {}", userName, url, resource.getFilename());
                this.ziroomDataSourceMap.put(sourceName, ziroomPropertySource);
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
