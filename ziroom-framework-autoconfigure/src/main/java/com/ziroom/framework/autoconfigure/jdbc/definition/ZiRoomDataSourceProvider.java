package com.ziroom.framework.autoconfigure.jdbc.definition;

import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiRoomDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;


/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
@Configuration
public class ZiRoomDataSourceProvider {

    private static final Log log = LogFactory.getLog(ZiRoomDataSourceProvider.class);

    public static final String DATASOURCE_CLASSPATH = "/app/config/{$appid}-datasource.properties";

    private ZiRoomDataSource ziRoomDataSource;

    public void initialize()  {

    }

    @PostConstruct
    public ZiRoomDataSource initZiRoomDataSource(){

        return ziRoomDataSource = new ZiRoomDataSource();
    }

    public ZiRoomDataSource getZiRoomDataSource(){
        return  ziRoomDataSource;
    }

}
