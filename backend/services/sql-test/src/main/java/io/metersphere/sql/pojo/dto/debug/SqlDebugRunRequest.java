package io.metersphere.sql.pojo.dto.debug;

import io.metersphere.sql.pojo.data.source.DataSourceBaseRequest;
import io.metersphere.sql.pojo.dto.SqlRequestParams;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SqlDebugRunRequest extends DataSourceBaseRequest {
    @Schema(description = "SQL ID")
    private String id;

    // TODO：暂未明确作用，从API复制来的
    @Schema(description = "报告ID，传了可以实时获取结果，不传则不支持实时获取 ")
    private String reportId;

    @NotNull
    @Schema(description = "请求内容，唯一内容为 sql-string")
    private SqlRequestParams request;

    @Schema(description = "项目ID")
    private String projectId;

    @Schema(description = "是否是本地执行")
    private Boolean frontendDebug = false;


}
