package io.metersphere.sql.pojo.dto.debug;

import io.metersphere.sql.controller.data.source.request.DataSourceBaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SqlDebugRunRequest extends DataSourceBaseRequest {
    @Schema(description = "SQL ID")
    private String id;

    // TODO：暂未明确作用，从API复制来的
    @Schema(description = "报告ID，传了可以实时获取结果，不传则不支持实时获取 ")
    private String reportId;

    @NotNull
    @Schema(description = "请求内容，唯一内容为sql-string")
    private RequestParams request;

    @Schema(description = "项目ID")
    private String projectId;

    @Schema(description = "是否是本地执行")
    private Boolean frontendDebug = false;

    @Data
    public static class RequestParams {
        private SqlExecuteBody body;
    }

    @Data
    public static class SqlExecuteBody {
        private String sqlContent;
    }

}
