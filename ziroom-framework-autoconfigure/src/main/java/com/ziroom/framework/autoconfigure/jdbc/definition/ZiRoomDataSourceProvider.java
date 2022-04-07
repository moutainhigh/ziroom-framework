package com.ziroom.framework.autoconfigure.jdbc.definition;

import com.ziroom.framework.autoconfigure.common.CommonMixUtils;
import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiRoomDataSource;
import com.ziroom.framework.autoconfigure.utils.BOMInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
public class ZiRoomDataSourceProvider {

    private static final Log log = LogFactory.getLog(ZiRoomDataSourceProvider.class);

    private static final String DATASOURCE_PREFIX= "datasource";

    private Map<String,ZiRoomDataSource> ziRoomDataSourceMap = new HashMap<>();

    public void initialize() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:app/conf/*.properties");
            for(Resource resource : resources){
                if (!resource.getFilename().contains(DATASOURCE_PREFIX)){
                    continue;
                }
                Properties dataSourceProperties = new Properties();
                dataSourceProperties.load(new InputStreamReader(new BOMInputStream(resource.getInputStream()), StandardCharsets.UTF_8));
                //TODO
                String appId = dataSourceProperties.getProperty("appId");
                String prefix = dataSourceProperties.getProperty("prefix");
                String configClassify = dataSourceProperties.getProperty("config.classify");
                String propertiesName = dataSourceProperties.getProperty("properties.name");
                String propertiesUrl = dataSourceProperties.getProperty("properties.url");
                String propertiesDriver = dataSourceProperties.getProperty("properties.driver-class-name","com.mysql.cj.jdbc.Driver");
                String propertiesUsername = dataSourceProperties.getProperty("properties.username");
                String propertiesPassword = dataSourceProperties.getProperty("properties.password");
                ZiRoomDataSource dataSource = new ZiRoomDataSource();
                dataSource.setAppId(appId);
                dataSource.setPrefix(prefix);
                ZiRoomDataSource.Config config = new ZiRoomDataSource.Config();
                config.setClassify(configClassify);
                dataSource.setConfig(config);
                ZiRoomDataSource.Properties properties = new ZiRoomDataSource.Properties();
                properties.setName(propertiesName);
                properties.setPassword(propertiesPassword);
                properties.setUrl(propertiesUrl);
                properties.setUsername(propertiesUsername);
                properties.setDriver(propertiesDriver);
                dataSource.setProperties(properties);
                if (CommonMixUtils.isNotBlank(appId)&&CommonMixUtils.isNotBlank(prefix)&&
                        CommonMixUtils.isNotBlank(propertiesName)&&CommonMixUtils.isNotBlank(propertiesUrl)&&
                        CommonMixUtils.isNotBlank(propertiesUsername)&&CommonMixUtils.isNotBlank(propertiesPassword)){
                    log.info(String.format("appId %s的数据源链接信息已被omega配置文件覆盖，详细参数请查看omega环境配置文件",appId));
                    this.ziRoomDataSourceMap.put(propertiesName,dataSource);
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
