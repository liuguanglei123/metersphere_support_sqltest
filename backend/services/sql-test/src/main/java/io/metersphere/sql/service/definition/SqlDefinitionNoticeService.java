package io.metersphere.sql.service.definition;

import io.metersphere.sdk.util.BeanUtils;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionAddRequest;
import io.metersphere.system.dto.sdk.ApiDefinitionCaseDTO;
import io.metersphere.system.dto.sdk.SqlDefinitionCaseDTO;
import org.springframework.stereotype.Service;

@Service
public class SqlDefinitionNoticeService {

    public SqlDefinitionCaseDTO getApiDTO(SqlDefinitionAddRequest request) {
        SqlDefinitionCaseDTO caseDTO = new SqlDefinitionCaseDTO();
        BeanUtils.copyBean(caseDTO, request);
        return caseDTO;
    }
}
