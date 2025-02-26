package io.metersphere.sql.pojo.model;

import io.metersphere.sql.pojo.dto.debug.SqlDebugRunRequest;
import io.metersphere.sql.pojo.params.DlExecuteParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class Command implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * sql statement
     */
    @Deprecated
    private String script;

    /**
     * console id
     */
    @NotNull
    private Long consoleId;

    /**
     * Data source id
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB name
     */
    @NotNull
    private String databaseName;

    /**
     * schema name
     */
    private String schemaName;

    /**
     *
     */
    private String tableName;

    /**
     *Page coding
      * Only available for select statements
     */
    private Integer pageNo;

    /**
     * Paging Size
      * Only available for select statements
     */
    private Integer pageSize;

    /**
     * Return all data
     * Only available for select statements
     */
    private Boolean pageSizeAll;

    /**
     * single SQL
     */
    private boolean single;

    private String id;

    // TODO：暂未明确作用，从API复制来的
    @Schema(description = "报告ID，传了可以实时获取结果，不传则不支持实时获取 ")
    private String reportId;

    @NotNull
    @Schema(description = "请求内容，唯一内容为sql-string")
    private SqlDebugRunRequest.RequestParams request;

    @Schema(description = "项目ID")
    private String projectId;

    @Schema(description = "是否是本地执行")
    private Boolean frontendDebug = false;
}
