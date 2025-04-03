package io.metersphere.sql.mapper;

import io.metersphere.api.domain.ApiDefinitionModule;
import io.metersphere.project.dto.ModuleCountDTO;
import io.metersphere.sql.domain.SqlDefinitionModule;
import io.metersphere.sql.pojo.dto.SqlTreeNode;
import io.metersphere.sql.pojo.dto.definition.SqlModuleRequest;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import io.metersphere.system.interceptor.BaseConditionFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSqlDefinitionModuleMapper {
    List<BaseTreeNode> selectBaseByRequest(@Param("request") SqlModuleRequest request);

    @BaseConditionFilter
    List<SqlTreeNode> selectSqlDataByRequest(@Param("request") SqlModuleRequest request, @Param("deleted") boolean deleted);

    Long getMaxPosByParentId(String parentId);

    List<SqlDefinitionModule> getNameInfoByIds(@Param("ids") List<String> ids);

    List<BaseTreeNode> selectNodeByIds(@Param("ids") List<String> ids);

    void deleteByIds(@Param("ids") List<String> deleteId);

    List<String> selectChildrenIdsByParentIds(@Param("ids") List<String> deleteIds);

    @BaseConditionFilter
    List<ModuleCountDTO> countModuleIdByRequest(@Param("request") SqlModuleRequest request, @Param("deleted") boolean deleted, @Param("isRepeat") boolean isRepeat);

    List<BaseTreeNode> selectIdAndParentIdByRequest(@Param("request") SqlModuleRequest request);

}
