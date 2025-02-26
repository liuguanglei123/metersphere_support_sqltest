package io.metersphere.sql.mapper;

import io.metersphere.project.dto.ModuleCountDTO;
import io.metersphere.project.dto.NodeSortQueryParam;
import io.metersphere.sql.pojo.dto.SqlDebugRequest;
import io.metersphere.sql.pojo.dto.SqlTreeNode;
import io.metersphere.system.dto.sdk.BaseModule;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSqlDebugModuleMapper {
    List<BaseTreeNode> selectBaseByUser(String userId);

    List<BaseTreeNode> selectIdAndParentIdByUserId(String userId);

    List<String> selectChildrenIdsByParentIds(@Param("ids") List<String> deleteIds);

    List<BaseTreeNode> selectBaseNodeByIds(@Param("ids") List<String> ids);

    List<String> selectChildrenIdsSortByPos(String parentId);

    void deleteByIds(@Param("ids") List<String> deleteId);

    Long getMaxPosByParentId(String parentId);

    BaseModule selectBaseModuleById(String dragNodeId);

    BaseModule selectModuleByParentIdAndPosOperator(NodeSortQueryParam nodeSortQueryParam);

    List<SqlTreeNode> selectSqlDebugByUser(String userId);

    List<ModuleCountDTO> countModuleIdByKeyword(@Param("request") SqlDebugRequest request, @Param("userId") String userId);
}
