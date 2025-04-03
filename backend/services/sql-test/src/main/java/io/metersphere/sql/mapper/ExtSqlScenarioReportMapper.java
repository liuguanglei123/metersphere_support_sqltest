package io.metersphere.sql.mapper;

import io.metersphere.api.domain.ApiScenarioReport;
import io.metersphere.sql.domain.SqlScenarioReport;
import io.metersphere.sql.pojo.dto.report.SqlReportPageRequest;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioReportStepDTO;
import io.metersphere.system.interceptor.BaseConditionFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSqlScenarioReportMapper {
    @BaseConditionFilter
    List<SqlScenarioReport> list(@Param("request") SqlReportPageRequest request);

    List<SqlScenarioReportStepDTO> selectStepByReportId(@Param("reportId") String reportId);

    List<SqlScenarioReportStepDTO> selectStepDetailByReportId(@Param("id") String id);

}
