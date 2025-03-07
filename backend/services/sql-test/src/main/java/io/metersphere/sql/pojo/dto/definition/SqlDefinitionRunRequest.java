package io.metersphere.sql.pojo.dto.definition;

import io.metersphere.sql.pojo.dto.debug.SqlDebugRunRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class SqlDefinitionRunRequest extends SqlDebugRunRequest {
    @Schema(description = "环境ID")
    private String environmentId;

    @Schema(description = "SQL语句类型，如DDL DQL等 TODO：可以列一个枚举值限制下")
    private String method;

    @Schema(description = "模块fk")
    private String moduleId;

    @Schema(description = "接口编号  mock执行需要")
    private Long num;
}
