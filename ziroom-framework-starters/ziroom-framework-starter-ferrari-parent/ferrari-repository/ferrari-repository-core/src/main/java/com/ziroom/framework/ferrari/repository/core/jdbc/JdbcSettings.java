package com.ziroom.framework.ferrari.repository.core.jdbc;

import com.google.common.base.MoreObjects;
import com.ziroom.framework.ferrari.repository.core.DaoSettings;
import com.ziroom.framework.ferrari.repository.core.constant.DialectEnum;

import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;

/**
 * @Author: J.T.
 * @Date: 2021/8/27 17:29
 * @Version 1.0
 */
public class JdbcSettings implements DaoSettings {
    private DialectEnum dialectEnum;
    private List<DataSource> writeDataSource;
    private List<DataSource> readDataSource;

    public DialectEnum getDialectEnum() {
        return dialectEnum;
    }

    public void setDialectEnum(DialectEnum dialectEnum) {
        this.dialectEnum = dialectEnum;
    }

    public List<DataSource> getWriteDataSource() {
        return writeDataSource;
    }

    public void setWriteDataSource(List<DataSource> writeDataSource) {
        this.writeDataSource = writeDataSource;
    }

    public List<DataSource> getReadDataSource() {
        return readDataSource;
    }

    public void setReadDataSource(List<DataSource> readDataSource) {
        this.readDataSource = readDataSource;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dialectEnum", dialectEnum)
                .add("writeDataSource", writeDataSource)
                .add("readDataSource", readDataSource)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JdbcSettings that = (JdbcSettings) o;
        return dialectEnum == that.dialectEnum &&
                Objects.equals(writeDataSource, that.writeDataSource) &&
                Objects.equals(readDataSource, that.readDataSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dialectEnum, writeDataSource, readDataSource);
    }
}

