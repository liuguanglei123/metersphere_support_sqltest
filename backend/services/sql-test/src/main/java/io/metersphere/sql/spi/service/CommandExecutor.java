package io.metersphere.sql.spi.service;

import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.sql.pojo.model.Command;
import io.metersphere.sql.pojo.model.DiffExecuteResult;
import io.metersphere.sql.pojo.model.ExecuteResult;
import io.metersphere.sql.pojo.request.MsSqlCaseElement;

import java.sql.Connection;
import java.util.List;

/**
 * Command executor
 * <p>
 * The command executor is used to execute the command.
 * <br>
 */
public interface CommandExecutor {

    /**
     * Execute command
     */
    List<ExecuteResult> executeDirect(Command command);

    /**
     * Execute command
     */
    List<DiffExecuteResult> executeDirectAndDiff(MsSqlCaseElement command);

    List<ExecuteResult> execute(Command command, Connection connection);

    /**
     * 向kafka中发送待执行的任务消息
     */
    void sendTaskMessage(Command command);

    /**
     * 向kafka中发送待执行的任务消息
     */
    void sendSqlScenarioRunTaskMessage(SqlAbstractMsTestElement command);

}
