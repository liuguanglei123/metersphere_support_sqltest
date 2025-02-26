package io.metersphere.sql.spi.service.impl.dbManage;

import io.metersphere.sql.spi.service.DBManage;
import io.metersphere.sql.spi.service.impl.commandExecutorImpl.SQLExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class MysqlDBManage extends DefaultDBManage implements DBManage {

    private static String PROCEDURE_SQL = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.ROUTINES " +
            "WHERE ROUTINE_SCHEMA = '%s' AND ROUTINE_NAME = '%s' AND ROUTINE_TYPE = 'PROCEDURE'";

    public MysqlDBManage(){
        super();
    }
    @Override
    public void connectDatabase(Connection connection, String database) {
        if (StringUtils.isEmpty(database)) {
            return;
        }
        try {
            SQLExecutor.getInstance().execute(connection, "use `" + database + "`;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
