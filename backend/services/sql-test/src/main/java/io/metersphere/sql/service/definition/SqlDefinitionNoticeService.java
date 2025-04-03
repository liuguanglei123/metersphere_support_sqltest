package io.metersphere.sql.service.definition;

import io.metersphere.api.domain.ApiDefinition;
import io.metersphere.sdk.util.BeanUtils;
import io.metersphere.sql.domain.SqlDefinition;
import io.metersphere.sql.mapper.SqlDefinitionMapper;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionAddRequest;
import io.metersphere.system.dto.sdk.ApiDefinitionCaseDTO;
import io.metersphere.system.dto.sdk.SqlDefinitionCaseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SqlDefinitionNoticeService {

    @Autowired
    SqlDefinitionMapper sqlDefinitionMapper;

    public SqlDefinitionCaseDTO getApiDTO(SqlDefinitionAddRequest request) {
        SqlDefinitionCaseDTO caseDTO = new SqlDefinitionCaseDTO();
        BeanUtils.copyBean(caseDTO, request);
        return caseDTO;
    }

    public SqlDefinitionCaseDTO getDeleteApiDTO(String id) {
        SqlDefinitionCaseDTO caseDTO = new SqlDefinitionCaseDTO();
        SqlDefinition sqlDefinition = sqlDefinitionMapper.selectByPrimaryKey(id);
        BeanUtils.copyBean(caseDTO, sqlDefinition);
        return caseDTO;
    }
}
