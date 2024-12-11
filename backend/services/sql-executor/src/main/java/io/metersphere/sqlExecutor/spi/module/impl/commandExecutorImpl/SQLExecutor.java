package io.metersphere.sqlExecutor.spi.module.impl.commandExecutorImpl;

import cn.hutool.core.date.TimeInterval;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.google.common.collect.Lists;
import io.metersphere.sqlExecutor.constant.EasyToolsConstant;
import io.metersphere.sqlExecutor.context.Chat2DBContext;
import io.metersphere.sqlExecutor.enums.SqlTypeEnum;
import io.metersphere.sqlExecutor.excption.BusinessException;
import io.metersphere.sqlExecutor.pojo.model.Command;
import io.metersphere.sqlExecutor.pojo.model.JDBCDataValue;
import io.metersphere.sqlExecutor.spi.Header;
import io.metersphere.sqlExecutor.spi.module.CommandExecutor;
import io.metersphere.sqlExecutor.spi.module.ValueProcessor;
import io.metersphere.sqlExecutor.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.sql.*;
import java.util.*;
import io.metersphere.sqlExecutor.enums.DataTypeEnum;

import io.metersphere.sqlExecutor.pojo.model.ExecuteResult;

/**
 * Dbhub unified database connection management
 *
 * @author jipengfei
 */
@Slf4j
public class SQLExecutor implements CommandExecutor {

    /**
     * Singleton instance of SQLExecutor.
     */
    private static final SQLExecutor INSTANCE = new SQLExecutor();

    public SQLExecutor() {
    }

    public static SQLExecutor getInstance() {
        return INSTANCE;
    }

    @Override
    public List<ExecuteResult> execute(Command command) {
        if (StringUtils.isBlank(command.getScript())) {
            return Collections.emptyList();
        }
        // parse sql
        String type = Chat2DBContext.getConnectInfo().getDbType();
        DbType dbType = JdbcUtils.parse2DruidDbType(type);
        List<String> sqlList = Lists.newArrayList(command.getScript());
        if(!command.isSingle()) {
            sqlList = SqlUtils.parse(command.getScript(), dbType, true);
        }
        if (CollectionUtils.isEmpty(sqlList)) {
            throw new BusinessException("dataSource.sqlAnalysisError");
        }
        List<ExecuteResult> result = new ArrayList<>();
        // Execute SQL
        for (String originalSql : sqlList) {
            ExecuteResult executeResult = executeSQL(originalSql, dbType, command);
            result.add(executeResult);
        }
        return result;
    }

    private ExecuteResult executeSQL(String originalSql, DbType dbType, Command param) {
        int pageNo = Optional.ofNullable(param.getPageNo()).orElse(1);
        int pageSize = Optional.ofNullable(param.getPageSize()).orElse(EasyToolsConstant.MAX_PAGE_SIZE);
        Integer offset = (pageNo - 1) * pageSize;
        Integer count = pageSize;
        SqlTypeEnum sqlType = getSqlType(dbType, originalSql);
        ExecuteResult executeResult = null;

        if (SqlTypeEnum.SELECT.equals(sqlType) && !SqlUtils.hasPageLimit(originalSql, dbType)) {
            // TODO: fix page limit
//            String pageLimit = Chat2DBContext.getSqlBuilder().pageLimit(originalSql, offset, pageNo, pageSize);
//            if (StringUtils.isNotBlank(pageLimit)) {
//                executeResult = execute(pageLimit, 0, count);
//            }
        }

        if (executeResult == null || !executeResult.getSuccess()) {
            executeResult = execute(originalSql, offset, count);
        }

        executeResult.setSqlType(sqlType.getCode());
        executeResult.setOriginalSql(originalSql);

        SqlUtils.buildCanEditResult(originalSql, dbType, executeResult);
        // Add row number
        addRowNumber(executeResult, pageNo, pageSize);
        //  Total number of fuzzy rows
        setPageInfo(executeResult, sqlType, pageNo, pageSize);
        return executeResult;
    }

    private SqlTypeEnum getSqlType(DbType dbType, String originalSql) {
        SqlTypeEnum sqlType = SqlTypeEnum.UNKNOWN;
        // parse sql
        String type = Chat2DBContext.getConnectInfo().getDbType();
        // TODO:
        boolean supportDruid = true;
//        boolean supportDruid = !DataSourceTypeEnum.MONGODB.getCode().equals(type);
        SQLStatement sqlStatement = null;
        if (supportDruid) {
            try {
                sqlStatement = SQLUtils.parseSingleStatement(originalSql, dbType);
            } catch (Exception e) {
                log.warn("Failed to parse sql: {}", originalSql, e);
            }
        }

        // Mongodb is currently unable to recognize it, so every time a page is transmitted
        if (!supportDruid || (sqlStatement instanceof SQLSelectStatement)) {
            sqlType = SqlTypeEnum.SELECT;
        }
        return sqlType;
    }

    private ExecuteResult execute(String sql, Integer offset, Integer count) {
        ExecuteResult executeResult;
        try {
            executeResult = SQLExecutor.getInstance().execute(sql, Chat2DBContext.getConnection(), true, offset, count);
        } catch (SQLException e) {
            log.error("Execute sql: {} exception", sql, e);
            executeResult = ExecuteResult.builder()
                    .sql(sql)
                    .success(Boolean.FALSE)
                    .message(e.getMessage())
                    .build();
        }
        return executeResult;
    }

    /**
     * Executes the given SQL query using the provided connection.
     *
     * @param sql          The SQL query to be executed.
     * @param connection   The database connection to use for the query.
     * @param limitRowSize Flag to indicate if row size should be limited.
     * @param offset       The starting point of rows to fetch in the result set.
     * @param count        The number of rows to fetch from the result set.
     * @return ExecuteResult containing the result of the execution.
     * @throws SQLException If there is any SQL related error.
     */
    public ExecuteResult execute(final String sql, Connection connection, boolean limitRowSize, Integer offset, Integer count)
            throws SQLException {
        Assert.notNull(sql, "SQL must not be null");
        ExecuteResult executeResult = ExecuteResult.builder().sql(sql).success(Boolean.TRUE).build();
        try (Statement stmt = connection.createStatement()) {
            stmt.setFetchSize(EasyToolsConstant.MAX_PAGE_SIZE);
            if (offset != null && count != null) {
                stmt.setMaxRows(offset + count);
            }
            TimeInterval timeInterval = new TimeInterval();
            boolean query = stmt.execute(sql);
            executeResult.setDescription(I18nUtils.getMessage("sqlResult.success"));
            // Represents the query
            if (query) {
                executeResult = generateQueryExecuteResult(stmt, limitRowSize, offset, count);
            } else {
                // Modification or other
                executeResult.setUpdateCount(stmt.getUpdateCount());
            }
            executeResult.setDuration(timeInterval.interval());
        }
        return executeResult;
    }

    private ExecuteResult generateQueryExecuteResult(Statement stmt, boolean limitRowSize, Integer offset,
                                                     Integer count) throws SQLException {
        ExecuteResult executeResult = ExecuteResult.builder().success(Boolean.TRUE).build();
        executeResult.setDescription(I18nUtils.getMessage("sqlResult.success"));
        ResultSet rs = null;
        try {
            rs = stmt.getResultSet();
            // Get how many columns
            ResultSetMetaData resultSetMetaData = rs.getMetaData();
            int col = resultSetMetaData.getColumnCount();
            // Get header information
            List<Header> headerList = generateHeaderList(resultSetMetaData);


            int chat2dbAutoRowIdIndex = getChat2dbAutoRowIdIndex(headerList);
            // Get data information
            List<List<String>> dataList = generateDataList(rs, col, chat2dbAutoRowIdIndex, limitRowSize,
                    offset, count);

            executeResult.setHeaderList(headerList);
            executeResult.setDataList(dataList);
        } finally {
            JdbcUtils.closeResultSet(rs);
        }
        return executeResult;
    }

    private List<Header> generateHeaderList(ResultSetMetaData resultSetMetaData) throws SQLException {
        int col = resultSetMetaData.getColumnCount();
        List<Header> headerList = Lists.newArrayListWithExpectedSize(col);
        for (int i = 1; i <= col; i++) {
            headerList.add(Header.builder()
                    .dataType(JdbcUtils.resolveDataType(
                            resultSetMetaData.getColumnTypeName(i), resultSetMetaData.getColumnType(i)).getCode())
                    .name(ResultSetUtils.getColumnName(resultSetMetaData, i))
                    .build());
        }
        return headerList;
    }

    private int getChat2dbAutoRowIdIndex(List<Header> headerList) {

        for (int i = 0; i < headerList.size(); i++) {
            Header header = headerList.get(i);
            if ("CAHT2DB_AUTO_ROW_ID".equals(header.getName())) {
                headerList.remove(i);
                return i + 1;
            }
        }
        return -1;
    }

    private List<List<String>> generateDataList(ResultSet rs, int col, int chat2dbAutoRowIdIndex,
                                                boolean limitRowSize, Integer offset, Integer count) throws SQLException {
        List<List<String>> dataList = Lists.newArrayList();

        if (offset == null || offset < 0) {
            offset = 0;
        }
        int rowNumber = 0;
        int rowCount = 1;
        while (rs.next()) {
            if (rowNumber++ < offset) {
                continue;
            }
            List<String> row = Lists.newArrayListWithExpectedSize(col);
            dataList.add(row);
            for (int i = 1; i <= col; i++) {
                if (chat2dbAutoRowIdIndex == i) {
                    continue;
                }
                ValueProcessor valueProcessor = Chat2DBContext.getMetaData().getValueProcessor();
                row.add(valueProcessor.getJdbcValue(new JDBCDataValue(rs, rs.getMetaData(), i, limitRowSize)));
            }
            if (count != null && count > 0 && rowCount++ >= count) {
                break;
            }
        }
        return dataList;
    }

    private void addRowNumber(ExecuteResult executeResult, int pageNo, int pageSize) {
        List<Header> headers = executeResult.getHeaderList();
        Header rowNumberHeader = Header.builder()
                .name(I18nUtils.getMessage("sqlResult.rowNumber"))
                .dataType(DataTypeEnum.CHAT2DB_ROW_NUMBER
                        .getCode()).build();
        executeResult.setHeaderList(EasyCollectionUtils.union(Arrays.asList(rowNumberHeader), headers));

        // Add row number
        if (executeResult.getDataList() != null) {
            int rowNumberIncrement = 1 + Math.max(pageNo - 1, 0) * pageSize;
            for (int i = 0; i < executeResult.getDataList().size(); i++) {
                List<String> row = executeResult.getDataList().get(i);
                List<String> newRow = Lists.newArrayListWithExpectedSize(row.size() + 1);
                newRow.add(Integer.toString(i + rowNumberIncrement));
                newRow.addAll(row);
                executeResult.getDataList().set(i, newRow);
            }
        }
    }

    private void setPageInfo(ExecuteResult executeResult, SqlTypeEnum sqlType, int pageNo, int pageSize) {
        if (SqlTypeEnum.SELECT.equals(sqlType)) {
            executeResult.setPageNo(pageNo);
            executeResult.setPageSize(pageSize);
            executeResult.setHasNextPage(
                    CollectionUtils.size(executeResult.getDataList()) >= executeResult.getPageSize());
        } else {
            executeResult.setPageNo(pageNo);
            executeResult.setPageSize(CollectionUtils.size(executeResult.getDataList()));
            executeResult.setHasNextPage(Boolean.FALSE);
        }
        executeResult.setFuzzyTotal(calculateFuzzyTotal(pageNo, pageSize, executeResult));
    }

    private String calculateFuzzyTotal(int pageNo, int pageSize, ExecuteResult executeResult) {
        int dataSize = CollectionUtils.size(executeResult.getDataList());
        if (pageSize <= 0) {
            return Integer.toString(dataSize);
        }
        int fuzzyTotal = Math.max(pageNo - 1, 0) * pageSize + dataSize;
        if (dataSize < pageSize) {
            return Integer.toString(fuzzyTotal);
        }
        return fuzzyTotal + "+";
    }
}
