package com.ziroom.framework.autoconfigure.jdbc.definition;

import com.ziroom.framework.autoconfigure.common.CommonMixUtils;
import com.ziroom.framework.autoconfigure.jdbc.PropertySourcesConstants;
import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiRoomDataSource;
import com.ziroom.framework.autoconfigure.utils.SpringInjector;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class ZiRoomDataSourceProvider {

    private static final Log log = LogFactory.getLog(ZiRoomDataSourceProvider.class);

    private static final String DATASOURCE_PREFIX= "datasource";

    private Map<String,ZiRoomDataSource> ziRoomDataSourceMap = new HashMap<>();

    private PathMatchingResourcePatternResolver resourcePatternResolver;

    public void initialize() {
        try {
            this.resourcePatternResolver = SpringInjector.getInstance(PathMatchingResourcePatternResolver.class);
            Resource[] resources = resourcePatternResolver.getResources("classpath:*.yaml");
            for(Resource resource : resources){
                if (!resource.getFilename().contains(DATASOURCE_PREFIX)){
                    continue;
                }
                Yaml yaml = new Yaml(new Constructor(ZiRoomDataSource.class));
                ZiRoomDataSource ziRoomDataSource = yaml.load(resource.getInputStream());
                if (StringUtils.isEmpty(ziRoomDataSource.getProperties().get(PropertySourcesConstants.DATA_DRIVER_CLASS_NAME))){
                    ziRoomDataSource.getProperties().put(PropertySourcesConstants.DATA_DRIVER_CLASS_NAME,"com.mysql.cj.jdbc.Driver");
                }
                if (StringUtils.isEmpty(ziRoomDataSource.getProperties().get(PropertySourcesConstants.DATA_TYPE))){
                    ziRoomDataSource.getProperties().put(PropertySourcesConstants.DATA_TYPE,"com.zaxxer.hikari.HikariDataSource");
                }
                if (CommonMixUtils.isNotBlank(ziRoomDataSource.getAppId())&&CommonMixUtils.isNotBlank(ziRoomDataSource.getPrefix())&&
                        CommonMixUtils.isNotBlank(ziRoomDataSource.getProperties().get(PropertySourcesConstants.DATA_NAME))
                        &&CommonMixUtils.isNotBlank(ziRoomDataSource.getProperties().get(PropertySourcesConstants.DATA_URL))&&
                        CommonMixUtils.isNotBlank(ziRoomDataSource.getProperties().get(PropertySourcesConstants.DATA_USERNAME))&&
                        CommonMixUtils.isNotBlank(ziRoomDataSource.getProperties().get(PropertySourcesConstants.DATA_PASSWORD))){
                    log.info(String.format("appId %s的数据源链接信息已被omega配置文件覆盖，详细参数请查看omega环境配置文件",ziRoomDataSource.getAppId()));
                    this.ziRoomDataSourceMap.put(ziRoomDataSource.getProperties().get(PropertySourcesConstants.DATA_NAME),ziRoomDataSource);
                }
            }
        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }


    public Map<String,ZiRoomDataSource> getZiRoomDataSourceMap(){
        return  this.ziRoomDataSourceMap;
    }

    public static void main(String[] args){
        new ZiRoomDataSourceProvider().initialize();
    }
}
