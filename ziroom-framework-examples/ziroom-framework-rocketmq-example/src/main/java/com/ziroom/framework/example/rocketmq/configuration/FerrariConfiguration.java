package com.ziroom.framework.example.rocketmq.configuration;

import com.ziroom.ferrari.repository.core.constant.DialectEnum;
import com.ziroom.ferrari.repository.core.jdbc.JdbcSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FerrariConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean(name = "masterOnlyJdbcSettings")
    public JdbcSettings masterOnlyJdbcSettings() {
        JdbcSettings jdbcSettings = new JdbcSettings();
        List<DataSource> writeDatasource = new ArrayList<>(1);
        writeDatasource.add(dataSource);
        List<DataSource> readDatasource = new ArrayList<>(1);
        readDatasource.add(dataSource);
        jdbcSettings.setWriteDataSource(writeDatasource);
        jdbcSettings.setReadDataSource(readDatasource);
        jdbcSettings.setDialectEnum(DialectEnum.MYSQL);
        return jdbcSettings;
    }

}
