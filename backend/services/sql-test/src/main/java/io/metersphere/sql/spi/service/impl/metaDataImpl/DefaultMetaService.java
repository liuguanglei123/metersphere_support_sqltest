package io.metersphere.sql.spi.service.impl.metaDataImpl;

import io.metersphere.sql.spi.service.CommandExecutor;
import io.metersphere.sql.spi.service.MetaData;
import io.metersphere.sql.spi.service.SqlBuilder;
import io.metersphere.sql.spi.service.ValueProcessor;
import io.metersphere.sql.spi.service.impl.commandExecutorImpl.SQLExecutor;
import io.metersphere.sql.spi.service.impl.sqlBuilder.DefaultSqlBuilder;
import io.metersphere.sql.spi.service.impl.valueProcessor.DefaultValueProcessor;

public class DefaultMetaService implements MetaData {
    @Override
    public CommandExecutor getCommandExecutor() {
        return SQLExecutor.getInstance();
    }

    @Override
    public ValueProcessor getValueProcessor() {
        return new DefaultValueProcessor();
    }

    @Override
    public SqlBuilder getSqlBuilder() {
        return new DefaultSqlBuilder();
    }
}
