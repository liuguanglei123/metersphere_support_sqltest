package io.metersphere.sql.mapper;

import io.metersphere.sql.pojo.dto.scenario.SqlScenarioModuleRequest;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSqlScenarioModuleMapper {
    List<BaseTreeNode> selectBaseByRequest(@Param("request") SqlScenarioModuleRequest request);

    List<BaseTreeNode> selectIdAndParentIdByRequest(@Param("request") SqlScenarioModuleRequest request);

    Long getMaxPosByParentId(String parentId);

}
