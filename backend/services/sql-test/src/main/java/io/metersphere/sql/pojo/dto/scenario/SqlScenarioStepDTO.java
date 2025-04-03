package io.metersphere.sql.pojo.dto.scenario;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SqlScenarioStepDTO  extends SqlScenarioStepCommonDTO<SqlScenarioStepDTO>  {
    @Schema(description = "场景id")
    private String scenarioId;

    @Schema(description = "序号")
    private Long sort;

    @Schema(description = "父级fk")
    private String parentId;
}
