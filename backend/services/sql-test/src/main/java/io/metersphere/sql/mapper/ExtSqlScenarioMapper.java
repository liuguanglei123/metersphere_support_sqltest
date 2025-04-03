package io.metersphere.sql.mapper;


import io.metersphere.project.dto.ModuleCountDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioModuleRequest;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioPageRequest;
import io.metersphere.system.interceptor.BaseConditionFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSqlScenarioMapper {

    List<ModuleCountDTO> countModuleIdByRequest(@Param("request") SqlScenarioModuleRequest request, @Param("deleted") boolean deleted);

    @BaseConditionFilter
    List<SqlScenarioDTO> list(@Param("request") SqlScenarioPageRequest request, @Param("isRepeat") boolean isRepeat, @Param("testPlanId") String testPlanId);

}
