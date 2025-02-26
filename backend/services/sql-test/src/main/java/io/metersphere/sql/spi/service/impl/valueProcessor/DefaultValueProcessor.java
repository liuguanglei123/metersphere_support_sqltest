package io.metersphere.sql.spi.service.impl.valueProcessor;

import io.metersphere.sql.pojo.model.JDBCDataValue;
import io.metersphere.sql.spi.service.ValueProcessor;

public class DefaultValueProcessor implements ValueProcessor {
    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        return convertJDBCValueByType(dataValue);
    }

    public String convertJDBCValueByType(JDBCDataValue dataValue) {
        return dataValue.getString();
    }
}
