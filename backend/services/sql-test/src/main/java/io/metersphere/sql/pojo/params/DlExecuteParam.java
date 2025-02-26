package io.metersphere.sql.pojo.params;

import io.metersphere.sql.pojo.dto.debug.SqlDebugRunRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author moji
 * @version DataSourceExecuteParam.java, v 0.1 October 14, 2022 13:53 moji Exp $
 * @date 2022/10/14
 */
@Data
public class DlExecuteParam {

    /**
     * sql statement
     */
    @Deprecated
    private String sql;

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
     * databaseName
     */
    @NotNull
    private String databaseName;


    private String tableName;


    /**
     * schema name
     */
    private String schemaName;

    /**
     * Page coding
     * Only the select statement has
     */
    private Integer pageNo;

    /**
     * Paging Size
     * Only the select statement has
     */
    private Integer pageSize;

    /**
     * Return all data
     * Only the select statement has
     */
    private Boolean pageSizeAll;

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
