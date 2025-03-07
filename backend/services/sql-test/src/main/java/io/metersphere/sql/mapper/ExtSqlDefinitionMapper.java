package io.metersphere.sql.mapper;

import io.metersphere.api.domain.ApiTestCase;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionDTO;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionPageRequest;
import io.metersphere.system.interceptor.BaseConditionFilter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExtSqlDefinitionMapper {
    @BaseConditionFilter
    List<SqlDefinitionDTO> list(@Param("request") SqlDefinitionPageRequest request);

    List<ApiTestCase> selectNotInTrashCaseIdsByApiIds(@Param("apiIds") List<String> apiIds);

}
