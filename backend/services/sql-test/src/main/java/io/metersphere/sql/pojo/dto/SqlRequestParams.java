package io.metersphere.sql.pojo.dto;

import io.metersphere.sql.pojo.dto.debug.SqlDebugRunRequest;
import lombok.Data;

@Data
public class SqlRequestParams {
    private SqlExecuteBody body;
    // TODO：未来这里可能会有auth等组件

    @Data
    public static class SqlExecuteBody {
        private String sqlContent;
        // TODO：未来这里可能会有pre post等sql语句
    }
}


