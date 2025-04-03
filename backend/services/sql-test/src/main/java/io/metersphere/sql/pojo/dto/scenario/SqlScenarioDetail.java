package io.metersphere.sql.pojo.dto.scenario;

import io.metersphere.sql.domain.SqlScenario;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SqlScenarioDetail extends SqlScenario {
    @Schema(description = "模块路径")
    private String modulePath;
    @Schema(description = "场景的通用配置")
    private ScenarioConfig scenarioConfig = new ScenarioConfig();
    @Schema(description = "步骤")
    private List<SqlScenarioStepDTO> steps;

//    public void resetConfigCsvId() {
//        if (scenarioConfig == null || scenarioConfig.getVariable() == null || scenarioConfig.getVariable().getCsvVariables() == null) {
//            return;
//        }
//
//        scenarioConfig.getVariable().getCsvVariables().forEach(csvVariable -> {
//            csvVariable.setId(IDGenerator.nextStr());
//        });
//    }
}
