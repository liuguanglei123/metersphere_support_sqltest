package io.metersphere.sqlExecutor.service.impl;


import io.metersphere.sqlExecutor.context.Chat2DBContext;
import io.metersphere.sqlExecutor.converter.CommandConverter;
import io.metersphere.sqlExecutor.pojo.model.Command;
import io.metersphere.sqlExecutor.pojo.model.ExecuteResult;
import io.metersphere.sqlExecutor.pojo.params.DlExecuteParam;
import io.metersphere.sqlExecutor.service.DlTemplateService;
import io.metersphere.sqlExecutor.spi.Header;
import io.metersphere.sqlExecutor.spi.module.CommandExecutor;
import io.metersphere.sqlExecutor.wrapper.result.ListResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DlTemplateServiceImpl implements DlTemplateService {

    @Autowired
    private CommandConverter commandConverter;

    @Override
    public ListResult<ExecuteResult> execute(DlExecuteParam param) {
        CommandExecutor executor = Chat2DBContext.getMetaData().getCommandExecutor();
        Command command = commandConverter.param2model(param);
        List<ExecuteResult> results = executor.execute(command);
        return reBuildHeader(results,param.getSchemaName(),param.getDatabaseName());
    }

    private ListResult<ExecuteResult> reBuildHeader(List<ExecuteResult> results,String schemaName,String databaseName){
        ListResult<ExecuteResult> listResult = ListResult.of(results);
        for (ExecuteResult executeResult : results) {
            List<Header> headers = executeResult.getHeaderList();
            if (executeResult.getSuccess() && executeResult.isCanEdit() && CollectionUtils.isNotEmpty(headers)) {
                headers = setColumnInfo(headers, executeResult.getTableName(), schemaName, databaseName);
                executeResult.setHeaderList(headers);
            }
            if (!executeResult.getSuccess()) {
                listResult.setSuccess(false);
                listResult.errorCode(executeResult.getDescription());
                listResult.setErrorMessage(executeResult.getMessage());
            }
            addOperationLog(executeResult);
        }
        return listResult;
    }

    private List<Header> setColumnInfo(List<Header> headers, String tableName, String schemaName, String databaseName) {
        // TODO:
//        try {
//            TableQueryParam tableQueryParam = new TableQueryParam();
//            tableQueryParam.setTableName(MetaNameUtils.getMetaName(tableName));
//            tableQueryParam.setSchemaName(schemaName);
//            tableQueryParam.setDatabaseName(databaseName);
//            tableQueryParam.setRefresh(true);
//            List<TableColumn> columns = tableService.queryColumns(tableQueryParam);
//            if (CollectionUtils.isEmpty(columns)) {
//                return headers;
//            }
//            Map<String, TableColumn> columnMap = columns.stream().collect(
//                    Collectors.toMap(TableColumn::getName, tableColumn -> tableColumn));
//            List<TableIndex> tableIndices = tableService.queryIndexes(tableQueryParam);
//            if (!CollectionUtils.isEmpty(tableIndices)) {
//                for (TableIndex tableIndex : tableIndices) {
//                    if ("PRIMARY".equalsIgnoreCase(tableIndex.getType())) {
//                        List<TableIndexColumn> columnList = tableIndex.getColumnList();
//                        if (!CollectionUtils.isEmpty(columnList)) {
//                            for (TableIndexColumn tableIndexColumn : columnList) {
//                                TableColumn tableColumn = columnMap.get(tableIndexColumn.getColumnName());
//                                if (tableColumn != null) {
//                                    tableColumn.setPrimaryKey(true);
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            for (Header header : headers) {
//                TableColumn tableColumn = columnMap.get(header.getName());
//                if (tableColumn != null) {
//                    header.setPrimaryKey(tableColumn.getPrimaryKey());
//                    header.setComment(tableColumn.getComment());
//                    header.setDefaultValue(tableColumn.getDefaultValue());
//                    header.setNullable(tableColumn.getNullable());
//                    header.setColumnSize(tableColumn.getColumnSize());
//                    header.setDecimalDigits(tableColumn.getDecimalDigits());
//                }
//            }
//
//        } catch (Exception e) {
//            log.error("setColumnInfo error:", e);
//        }
        return headers;
    }

    private void addOperationLog(ExecuteResult executeResult) {
        // TODO:
//        if (executeResult == null) {
//            return;
//        }
//        try {
//            ConnectInfo connectInfo = Chat2DBContext.getConnectInfo();
//            OperationLogCreateParam createParam = new OperationLogCreateParam();
//            createParam.setDdl(executeResult.getSql());
//            createParam.setStatus(executeResult.getSuccess() ? "success" : "fail");
//            createParam.setDatabaseName(connectInfo.getDatabaseName());
//            createParam.setDataSourceId(connectInfo.getDataSourceId());
//            createParam.setSchemaName(connectInfo.getSchemaName());
//            createParam.setUseTime(executeResult.getDuration());
//            createParam.setType(connectInfo.getDbType());
//            createParam.setOperationRows(
//                    executeResult.getUpdateCount() != null ? Long.valueOf(executeResult.getUpdateCount()) : null);
//            operationLogService.create(createParam);
//        } catch (Exception e) {
//            log.error("addOperationLog error:", e);
//        }
    }
}
