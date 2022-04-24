package com.ziroom.ferrari.repository.core.jdbc;

import com.google.common.collect.Lists;
import com.ziroom.ferrari.repository.core.DatabaseRouter;
import com.ziroom.ferrari.repository.core.transaction.TransactionContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:29
 * @Version 1.0
 */
public class JdbcTemplateRouter implements DatabaseRouter {
    private List<JdbcTemplate> writeJdbcTemplate = Lists.newArrayList();
    private List<JdbcTemplate> readJdbcTemplate = Lists.newArrayList();

    public JdbcTemplateRouter(JdbcSettings jdbcSettings) {
        //write
        List<DataSource> writeList = jdbcSettings.getWriteDataSource();
        if (CollectionUtils.isNotEmpty(writeList)) {
            for (DataSource dataSource : writeList) {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                writeJdbcTemplate.add(jdbcTemplate);
            }
        }

        //read
        List<DataSource> readList = jdbcSettings.getReadDataSource();
        if (CollectionUtils.isNotEmpty(readList)) {
            for (DataSource dataSource : readList) {
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
                readJdbcTemplate.add(jdbcTemplate);
            }
        }
    }

    @Override
    public Object writeRoute() {
        return writeJdbcTemplate.get(0);
    }

    @Override
    public Object readRoute() {
        if (TransactionContext.isInTransaction() || CollectionUtils.isEmpty(readJdbcTemplate)) {
            return this.writeRoute();
        }
        int randomPos = ThreadLocalRandom.current().nextInt(readJdbcTemplate.size());
        return readJdbcTemplate.get(randomPos);
    }
}
