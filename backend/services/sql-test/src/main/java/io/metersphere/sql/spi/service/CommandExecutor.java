package io.metersphere.sql.spi.service;

import io.metersphere.sql.pojo.model.Command;
import io.metersphere.sql.pojo.model.ExecuteResult;

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
