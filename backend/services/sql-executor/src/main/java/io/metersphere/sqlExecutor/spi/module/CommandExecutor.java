package io.metersphere.sqlExecutor.spi.module;

import io.metersphere.sqlExecutor.pojo.model.Command;
import io.metersphere.sqlExecutor.pojo.model.ExecuteResult;

import java.sql.Connection;
import java.sql.SQLException;
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
    List<ExecuteResult> execute(Command command);

}
