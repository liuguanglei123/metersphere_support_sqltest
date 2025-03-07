package io.metersphere.sql.mapper;

import io.metersphere.api.domain.ApiDefinitionModule;
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

}
