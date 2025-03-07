package io.metersphere.sql.service.definition;

import io.metersphere.sdk.constants.HttpMethodConstants;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionAddRequest;
import io.metersphere.system.log.constants.OperationLogModule;
import io.metersphere.system.log.constants.OperationLogType;
import io.metersphere.system.log.dto.LogDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class SqlDefinitionLogService {

    /**
     * 添加接口日志
     *
     * @param request
     * @return
     */
    public LogDTO addLog(SqlDefinitionAddRequest request) {
        LogDTO dto = new LogDTO(
                request.getProjectId(),
                null,
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.API_TEST_MANAGEMENT_DEFINITION,
                request.getName());

        dto.setHistory(true);
        dto.setMethod(HttpMethodConstants.POST.name());
        dto.setOriginalValue(JSON.toJSONBytes(request));
        return dto;
    }

}
