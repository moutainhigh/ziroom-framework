package com.ziroom.framework.autoconfigure.jdbc.definition;

import com.ziroom.framework.autoconfigure.common.ApplicationProvider;
import com.ziroom.framework.autoconfigure.common.CommonMixUtils;
import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiRoomDataSource;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
@Configuration
public class ZiRoomDataSourceProvider implements BeanClassLoaderAware, ApplicationContextInitializer {

    private static final Log log = LogFactory.getLog(ZiRoomDataSourceProvider.class);

    private static final String DATASOURCE_CLASSPATH = "/app/config/%s-datasource.properties";

    private ZiRoomDataSource ziRoomDataSource;

    private ClassLoader classLoader;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        try {
            ApplicationProvider appProvider = configurableApplicationContext.getBean(ApplicationProvider.class);
            InputStream in = classLoader.getResourceAsStream(String.format(DATASOURCE_CLASSPATH,appProvider.getAppId()));
            Properties dataSourceProperties = new Properties();
            dataSourceProperties.load(new InputStreamReader(new BOMInputStream(in), StandardCharsets.UTF_8));
            //TODO
            String appId = dataSourceProperties.getProperty("appId");
            String prefix = dataSourceProperties.getProperty("prefix");
            String configClassify = dataSourceProperties.getProperty("config.classify");
            String propertiesName = dataSourceProperties.getProperty("properties.name");
            String propertiesUrl = dataSourceProperties.getProperty("properties.url");
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
            dataSource.setProperties(properties);
            if (CommonMixUtils.isNotBlank(appId)&&CommonMixUtils.isNotBlank(prefix)&&
                    CommonMixUtils.isNotBlank(propertiesName)&&CommonMixUtils.isNotBlank(propertiesUrl)&&
                    CommonMixUtils.isNotBlank(propertiesUsername)&&CommonMixUtils.isNotBlank(propertiesPassword)){
                ziRoomDataSource = dataSource;
            }
        } catch (Throwable ex) {
            log.error("Initialize DefaultApplicationProvider failed.", ex);
        }
    }


    public ZiRoomDataSource getZiRoomDataSource(){
        return  ziRoomDataSource;
    }

}
