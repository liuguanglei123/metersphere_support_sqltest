package io.metersphere.sql.mapper;

import io.metersphere.api.domain.ApiScenarioCsvStep;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface ExtSqlScenarioStepMapper {
    List<SqlScenarioStepDTO> getStepDTOByScenarioIds(@Param("scenarioIds") List<String> scenarioIds);

    List<ApiScenarioCsvStep> getCsvStepByScenarioIds(@Param("scenarioIds") List<String> scenarioId);


}

