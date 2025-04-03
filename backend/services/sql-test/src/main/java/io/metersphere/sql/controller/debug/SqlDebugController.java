package io.metersphere.sql.controller.debug;

import io.metersphere.api.domain.ApiDebug;
import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sdk.dto.api.task.TaskRequestDTO;
import io.metersphere.sdk.dto.result.ListResult;
import io.metersphere.sql.converter.RdbWebConverter;
import io.metersphere.sql.aspect.ConnectionInfoAspect;
import io.metersphere.sql.domain.SqlDebug;
import io.metersphere.sql.pojo.dto.debug.SqlDebugAddRequest;
import io.metersphere.sql.pojo.dto.debug.SqlDebugDTO;
import io.metersphere.sql.pojo.dto.debug.SqlDebugRunRequest;
import io.metersphere.sql.pojo.dto.debug.SqlDebugUpdateRequest;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioDebugRequest;
import io.metersphere.sql.pojo.model.ExecuteResult;
import io.metersphere.sql.pojo.params.DlExecuteParam;
import io.metersphere.sql.pojo.request.DmlRequest;
import io.metersphere.sql.pojo.vo.ExecuteResultVO;
import io.metersphere.sql.service.common.DlTemplateService;
import io.metersphere.sql.service.debug.SqlDebugLogService;
import io.metersphere.sql.service.debug.SqlDebugService;
import io.metersphere.sql.service.scenario.SqlScenarioRunService;
import io.metersphere.system.log.annotation.Log;
import io.metersphere.system.log.constants.OperationLogType;
import io.metersphere.system.utils.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ConnectionInfoAspect
@RequestMapping("/sql/debug")
@RestController
public class SqlDebugController {
    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DlTemplateService dlTemplateService;

    @Autowired
    private SqlDebugService sqlDebugService;

    @Resource
    private SqlScenarioRunService sqlScenarioRunService;

    @PostMapping(value="/execute")
    public ListResult<ExecuteResultVO> executeDirect(@RequestBody SqlDebugRunRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        ListResult<? extends ExecuteResult> resultDTOListResult = dlTemplateService.executeDirect(param);
        List<ExecuteResultVO> resultVOS = rdbWebConverter.dto2vo(resultDTOListResult.getData());
        return ListResult.of(resultVOS);
    }

    @PostMapping("/debug")
    @Operation(summary = "运行SQL调试")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_EXECUTE)
    public ListResult<Void> execute(@RequestBody SqlDebugRunRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        dlTemplateService.execute(param);
        return ListResult.empty();
    }

    @PostMapping("/add")
    @Operation(summary = "创建SQL调试")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_ADD)
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = SqlDebugLogService.class)
    public SqlDebug add(@Validated @RequestBody SqlDebugAddRequest request) {
        return sqlDebugService.add(request, SessionUtils.getUserId());
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "获取SQL调试详情")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_READ)
    public SqlDebugDTO get(@PathVariable String id) {
        return sqlDebugService.get(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新SQL调试")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_UPDATE)
    @Log(type = OperationLogType.UPDATE, expression = "#msClass.updateLog(#request)", msClass = SqlDebugLogService.class)
    public SqlDebug update(@Validated @RequestBody SqlDebugUpdateRequest request) {
        return sqlDebugService.update(request, SessionUtils.getUserId());
    }


}
