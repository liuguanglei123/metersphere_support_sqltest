package io.metersphere.sql.pojo.dto.report;

import io.metersphere.sql.domain.SqlScenarioReport;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SqlScenarioReportListDTO extends SqlScenarioReport {
    @Schema(description = "创建人")
    private String createUserName;
    @Schema(description = "更新人")
    private String updateUserName;

}
