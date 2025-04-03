package io.metersphere.sql.mapper;

import io.metersphere.api.domain.ApiDefinition;
import io.metersphere.api.domain.ApiTestCase;
import io.metersphere.sql.domain.SqlDefinition;
import io.metersphere.sql.pojo.dto.SqlDefinitionExecuteInfo;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionDTO;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionPageRequest;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionVersionDTO;
import io.metersphere.system.interceptor.BaseConditionFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSqlDefinitionMapper {
    @BaseConditionFilter
    List<SqlDefinitionDTO> list(@Param("request") SqlDefinitionPageRequest request);

    List<String> getRefIds(@Param("ids") List<String> ids, @Param("deleted") boolean deleted);

    List<String> getIdsByRefId(@Param("refIds") List<String> refIds, @Param("deleted") boolean deleted);

    void batchDeleteByRefId(@Param("refIds") List<String> refIds, @Param("userId") String userId, @Param("projectId") String projectId);

    List<SqlDefinitionVersionDTO> getSqlDefinitionByRefId(@Param("refId") String refId);

    void batchDeleteById(@Param("ids") List<String> ids, @Param("userId") String userId, @Param("projectId") String projectId);

    void clearLatestVersion(@Param("refId") String refId, @Param("projectId") String projectId);

    void updateLatestVersion(@Param("id") String id, @Param("projectId") String projectId);

    void deleteSqlToGc(@Param("ids") List<String> ids, @Param("userId") String userId, @Param("time") long time);

    List<SqlDefinition> selectAllSql(@Param("projectId") String projectId);

    List<SqlDefinition> getListBySelectModules(@Param("projectId") String projectId, @Param("moduleIds") List<String> moduleIds);

    List<SqlDefinition> getListBySelectIds(@Param("projectId") String projectId, @Param("ids") List<String> ids);

    List<SqlDefinitionExecuteInfo> getSqlDefinitionExecuteInfo(@Param("ids") List<String> ids);

}
