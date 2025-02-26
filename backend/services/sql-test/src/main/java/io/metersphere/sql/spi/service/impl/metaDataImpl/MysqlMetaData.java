package io.metersphere.sql.spi.service.impl.metaDataImpl;

import io.metersphere.sql.spi.service.MetaData;
import io.metersphere.sql.spi.service.SqlBuilder;
import io.metersphere.sql.spi.service.ValueProcessor;
import io.metersphere.sql.spi.service.impl.sqlBuilder.MysqlSqlBuilder;
import io.metersphere.sql.spi.service.impl.valueProcessor.MysqlValueProcessor;

import java.util.Arrays;
import java.util.List;

public class MysqlMetaData extends DefaultMetaService implements MetaData {

    private List<String> systemDatabases = Arrays.asList("information_schema", "performance_schema", "mysql", "sys");

    @Override
    public SqlBuilder getSqlBuilder() {
        return new MysqlSqlBuilder();
    }

    @Override
    public ValueProcessor getValueProcessor() {
        return new MysqlValueProcessor();
    }
}
