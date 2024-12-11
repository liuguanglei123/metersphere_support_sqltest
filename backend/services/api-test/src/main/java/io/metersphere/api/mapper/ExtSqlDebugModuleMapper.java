package io.metersphere.api.mapper;

import io.metersphere.api.dto.debug.ApiDebugRequest;
import io.metersphere.api.dto.debug.ApiTreeNode;
import io.metersphere.api.dto.debug.SqlTreeNode;
import io.metersphere.project.dto.ModuleCountDTO;
import io.metersphere.project.dto.NodeSortQueryParam;
import io.metersphere.system.dto.sdk.BaseModule;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSqlDebugModuleMapper {

    List<BaseTreeNode> selectBaseByProtocolAndUser(String userId);

    List<SqlTreeNode> selectSqlDebugByProtocolAndUser(String userId);
}
