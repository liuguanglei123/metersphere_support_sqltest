package io.metersphere.sql.spi.service.impl.valueProcessor;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.metersphere.sql.pojo.model.JDBCDataValue;

import java.util.Objects;
import java.util.Set;

public class MysqlValueProcessor extends DefaultValueProcessor{
    public static final Set<String> FUNCTION_SET = Set.of("now()", "default");
    private static final Logger log = LoggerFactory.getLogger(MysqlValueProcessor.class);

    @Override
    public String getJdbcValue(JDBCDataValue dataValue) {
        Object value = dataValue.getObject();
        if (Objects.isNull(value)) {
            // mysql -> example: [date]->0000-00-00
            String stringValue = dataValue.getStringValue();
            if (Objects.nonNull(stringValue)) {
                return stringValue;
            }
            return null;
        }
        if (value instanceof String emptyStr) {
            if (StringUtils.isBlank(emptyStr)) {
                return emptyStr;
            }
        }
        return convertJDBCValueByType(dataValue);
    }
}
