package io.metersphere.sql.service.common.impl;


import io.metersphere.sdk.constants.KafkaTopicConstants;
import io.metersphere.sdk.constants.MsgType;
import io.metersphere.sdk.dto.SocketMsgDTO;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.aspect.ConnectionInfoHandler;
import io.metersphere.sql.context.Chat2DBContext;
import io.metersphere.sql.converter.CommandConverter;
import io.metersphere.sql.pojo.model.Command;
import io.metersphere.sql.pojo.model.ExecuteResult;
import io.metersphere.sql.pojo.params.DlExecuteParam;
import io.metersphere.sql.service.common.DlTemplateService;
import io.metersphere.sql.spi.model.Header;
import io.metersphere.sql.spi.service.CommandExecutor;
import io.metersphere.sql.wrapper.result.ListResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.metersphere.sdk.constants.KafkaTopicConstants.SQL_REPORT_DEBUG_TASK_RESULT_TOPIC;

@Slf4j
@Service
public class DlTemplateServiceImpl implements DlTemplateService {

    public static final String SQL_TASK_CONSUME_ID = "MS-SQL-TASK-CONSUME";

    @Autowired
    private CommandConverter commandConverter;

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Resource
    ConnectionInfoHandler connectionInfoHandler;

    @Override
    public void execute(DlExecuteParam param) {
        CommandExecutor executor = Chat2DBContext.getMetaData().getCommandExecutor();
        Command command = commandConverter.param2model(param);
        executor.sendTaskMessage(command);
    }

    /**
     * 前端调用，直接执行sql，不经过kafka消息
     * @param param
     * @return
     */
    @Override
    public ListResult<ExecuteResult> executeDirect(DlExecuteParam param) {
        CommandExecutor executor = Chat2DBContext.getMetaData().getCommandExecutor();
        Command command = commandConverter.param2model(param);
        List<ExecuteResult> results = executor.executeDirect(command);
        return reBuildHeader(results,param.getSchemaName(),param.getDatabaseName());
    }

    @KafkaListener(id = SQL_TASK_CONSUME_ID, topics = KafkaTopicConstants.SQL_REPORT_DEBUG_TASK_TOPIC, groupId = SQL_TASK_CONSUME_ID + "_" + "${random.uuid}")
    public void sqlTaskConsume(ConsumerRecord<?, String> record) {
        // TODO：这里预期会做并发控制，对于自动化用例的回归，可以控制线程数量执行，对于个人用户约定线程上限等等
        //  如果是采用线程池方式，那么需要在线程池中取出线程后，预先清理一下连接信息，防止用错数据库？
        log.info("sqlTaskConsume once!!!");
        log.info(record.value());
        Command command = JSON.parseObject(record.value(), Command.class);
        // 通过不同的协议来获取处理器类型，使用合适的CommandExecutor类型来执行，当前只有mysql，后续可能会因为支持pg协议等增加更多内容
        CommandExecutor executor = Chat2DBContext.getMetaData(command.getProtocol()).getCommandExecutor();

        Integer dataSourceId = command.getDataSourceId();
        Chat2DBContext.putContext(connectionInfoHandler.toInfo(dataSourceId));

        List<ExecuteResult> results = executor.executeDirect(command);
        ListResult<ExecuteResult> executeResultListResult = reBuildHeader(results, command.getSchemaName(), command.getDatabaseName());

        SocketMsgDTO socketMsgDTO = new SocketMsgDTO(command.getReportId(), "UNKNOW RUNMODE", MsgType.SQL_EXEC_RESULT.name(), executeResultListResult);

        kafkaTemplate.send(SQL_REPORT_DEBUG_TASK_RESULT_TOPIC,command.getReportId(),JSON.toJSONString(socketMsgDTO));

        // TODO：在原版ms中，ws连接与前端交互有两个重要的消息通知，一个是EXEC_RESULT,另一个是EXEC_END，前者是将结果通知到前端，后者是告诉前端需要关闭连接
        // 暂时先保留两个连接的逻辑，目前能想到的方案是，SQL_EXEC_RESULT用来通知结果，SQL_EXEC_END发送前则进行一些清理操作，当前现在都是在SQL_EXEC_RESULT就完成了
        // 后续如果仅需要一个通知的话，可以对前端进行改造仅保留一个通知就够了。
        SocketMsgDTO socketMsgDTO2 = new SocketMsgDTO(command.getReportId(), "UNKNOW RUNMODE", MsgType.SQL_EXEC_END.name(), JSON.toJSONString(executeResultListResult));

        kafkaTemplate.send(SQL_REPORT_DEBUG_TASK_RESULT_TOPIC,command.getReportId(),JSON.toJSONString(socketMsgDTO2));
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
