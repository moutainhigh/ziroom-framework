package com.ziroom.framework.autoconfigure.jdbc.definition;

import com.ziroom.framework.autoconfigure.jdbc.definition.domain.ZiRoomDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @Description
 * @Author lidm
 * @Date 2020/11/4
 */
public class ZiRoomDataSourceProvider {

    private static final Log log = LogFactory.getLog(ZiRoomDataSourceProvider.class);

    public static final String DATASOURCE_CLASSPATH = "/app/config/{$appid}-datasource.properties";

    protected ZiRoomDataSource ziRoomDataSource = null;

    public void initialize()  {


    }

    protected ZiRoomDataSource getZiRoomDataSource(){

        return ziRoomDataSource;
    }

}
