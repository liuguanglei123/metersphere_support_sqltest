package io.metersphere.sqlExecutor.spi.module;

import io.metersphere.sqlExecutor.pojo.model.JDBCDataValue;

public interface ValueProcessor {

    /**
     * 将JDBC数据值对象转换为适合前端展示的字符串格式。
     * <p>
     * 它旨在处理包括但不限于数字、日期、字符串以及特殊的空数据，确保这些数据
     * 在传递到前端用户界面时是格式化良好且可理解的。
     *
     * @param dataValue ResultSetMetaData, ResultSet, columnIndex的组合对象，用于获取数据值。
     * @return 一个格式化后的字符串，适配于前端展示。例如，日期可能会转换为"YYYY-MM-DD"格式，以方便用户直观理解。
     */
    String getJdbcValue(JDBCDataValue dataValue);
}