package io.metersphere.sql.service.debug;

import io.metersphere.sdk.constants.OperationLogConstants;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.domain.SqlDebug;
import io.metersphere.sql.pojo.dto.debug.SqlDebugAddRequest;
import io.metersphere.sql.pojo.dto.debug.SqlDebugUpdateRequest;
import io.metersphere.system.log.constants.OperationLogModule;
import io.metersphere.system.log.constants.OperationLogType;
import io.metersphere.system.log.dto.LogDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jianxing
 * @date : 2023-11-6
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SqlDebugLogService {

    @Resource
    private SqlDebugService sqlDebugService;

    public LogDTO addLog(SqlDebugAddRequest request) {
        LogDTO dto = new LogDTO(
                request.getProjectId(),
                null,
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.SQL_TEST_DEBUG_MANAGEMENT_DEBUG,
                request.getName());
        dto.setOriginalValue(JSON.toJSONBytes(request));
        return dto;
    }

    public LogDTO updateLog(SqlDebugUpdateRequest request) {
        SqlDebug sqlDebug = sqlDebugService.get(request.getId());
        LogDTO dto = null;
        if (sqlDebug != null) {
            dto = new LogDTO(
                    sqlDebug.getProjectId(),
                    null,
                    sqlDebug.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SQL_TEST_DEBUG_MANAGEMENT_DEBUG,
                    sqlDebug.getName());
            dto.setOriginalValue(JSON.toJSONBytes(sqlDebug));
        }
        return dto;
    }

    public LogDTO deleteLog(String id) {
        SqlDebug sqlDebug = sqlDebugService.get(id);
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                sqlDebug.getId(),
                null,
                OperationLogType.DELETE.name(),
                OperationLogModule.API_TEST_DEBUG_MANAGEMENT_DEBUG,
                sqlDebug.getName());
        dto.setOriginalValue(JSON.toJSONBytes(sqlDebug));
        return dto;
    }

    public LogDTO moveLog(String id) {
        SqlDebug sqlDebug = sqlDebugService.get(id);
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                sqlDebug.getId(),
                null,
                OperationLogType.UPDATE.name(),
                OperationLogModule.SQL_TEST_DEBUG_MANAGEMENT_DEBUG,
                sqlDebug.getName());
        dto.setOriginalValue(JSON.toJSONBytes(sqlDebug));
        return dto;
    }
}