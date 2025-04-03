package io.metersphere.sql.pojo.dto.scenario;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SqlScenarioDebugRequest extends SqlScenarioParseParam {
    @Schema(description = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_scenario.id.not_blank}")
    @Size(max = 50, message = "{api_scenario.id.length_range}")
    private String id;

    @Schema(description = "报告ID，传了可以实时获取结果，不传则不支持实时获取")
    @Size(max = 50)
    private String reportId;

    @Valid
    @Schema(description = "步骤集合")
    private List<SqlScenarioStepRequest> steps;

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String projectId;

    @Schema(description = "是否是本地执行")
    private Boolean frontendDebug = false;
}
