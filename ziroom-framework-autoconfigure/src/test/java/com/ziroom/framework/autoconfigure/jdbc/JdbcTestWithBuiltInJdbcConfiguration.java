package com.ziroom.framework.autoconfigure.jdbc;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = JdbcTestWithBuiltInJdbcConfiguration.Config.class)
public class JdbcTestWithBuiltInJdbcConfiguration {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testLoadMultiDataSource() {
        Map<String, DataSource> dsMap = context.getBeansOfType(DataSource.class);
        assertEquals(2, dsMap.size());

        HikariDataSource db1 = (HikariDataSource) dsMap.get("db1");
        assertEquals(1000000L, db1.getConnectionTimeout());
    }

    @Configuration
    @Import(ZiroomDataSourceAutoConfiguration.class)
    static class Config {

    }

}
