package io.metersphere.sqlExecutor.spi.module.impl.valueProcessor;

import io.metersphere.sqlExecutor.pojo.model.JDBCDataValue;
import io.metersphere.sqlExecutor.spi.module.ValueProcessor;

public class DefaultValueProcessor implements ValueProcessor {
    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        return convertJDBCValueByType(dataValue);
    }

    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getString();
    }
}
