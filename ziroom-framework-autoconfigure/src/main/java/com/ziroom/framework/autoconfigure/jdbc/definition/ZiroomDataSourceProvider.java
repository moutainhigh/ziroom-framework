package com.ziroom.framework.autoconfigure.jdbc.definition;

import com.ziroom.framework.autoconfigure.common.CommonMixUtils;
import com.ziroom.framework.autoconfigure.jdbc.PropertyConstants;
import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiroomDataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.HashMap;
import java.util.Map;


/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
public class ZiroomDataSourceProvider {

    private static final Logger log = LoggerFactory.getLogger(ZiroomDataSourceProvider.class);

    private static final String DATASOURCE_PREFIX = "datasource";

    private Map<String, ZiroomDataSource> ziRoomDataSourceMap = new HashMap<>();

    public void initialize() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:*.yaml");
            for (Resource resource : resources) {
                if (resource.getFilename() == null || !resource.getFilename().startsWith(DATASOURCE_PREFIX)) {
                    continue;
                }

                // todo 支持yaml格式， 该模块单独抽离开， 设计成通用模块
                Yaml yaml = new Yaml(new Constructor(ZiroomDataSource.class));
                ZiroomDataSource ziroomDataSource = yaml.load(resource.getInputStream());

                String name = ziroomDataSource.getProperties().get(PropertyConstants.DATA_NAME);
                String url = ziroomDataSource.getProperties().get(PropertyConstants.DATA_URL);

                if (CommonMixUtils.isBlank(name)) {
                    log.warn("无效数据库配置[properties.name 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }
                if (CommonMixUtils.isBlank(url)) {
                    log.warn("无效数据库配置[properties.url 为空]，请检查配置文件：" + resource.getFilename());
                    continue;
                }

                if (StringUtils.isEmpty(ziroomDataSource.getProperties().get(PropertyConstants.DATA_DRIVER_CLASS_NAME))) {
                    ziroomDataSource.getProperties().put(PropertyConstants.DATA_DRIVER_CLASS_NAME, "com.mysql.cj.jdbc.Driver");
                }
                if (StringUtils.isEmpty(ziroomDataSource.getProperties().get(PropertyConstants.DATA_TYPE))) {
                    ziroomDataSource.getProperties().put(PropertyConstants.DATA_TYPE, "com.zaxxer.hikari.HikariDataSource");
                }

                log.info("注册数据源[{}][{}]. 加载自: {}", name, url, resource.getFilename());
                this.ziRoomDataSourceMap.put(name, ziroomDataSource);
            }
        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }


    public Map<String, ZiroomDataSource> getZiroomDataSourceMap() {
        return this.ziRoomDataSourceMap;
    }

    public static void main(String[] args) {
        new ZiroomDataSourceProvider().initialize();
    }
}
