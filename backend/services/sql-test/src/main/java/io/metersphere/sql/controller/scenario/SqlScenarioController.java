package io.metersphere.sql.controller.scenario;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.api.domain.ApiScenario;
import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sdk.dto.api.task.TaskRequestDTO;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.domain.SqlScenario;
import io.metersphere.sql.pojo.dto.scenario.*;
import io.metersphere.sql.service.scenario.SqlScenarioRunService;
import io.metersphere.sql.service.scenario.SqlScenarioService;
import io.metersphere.system.log.annotation.Log;
import io.metersphere.system.log.constants.OperationLogType;
import io.metersphere.system.notice.annotation.SendNotice;
import io.metersphere.system.notice.constants.NoticeConstants;
import io.metersphere.system.security.CheckOwner;
import io.metersphere.system.utils.PageUtils;
import io.metersphere.system.utils.Pager;
import io.metersphere.system.utils.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sql/scenario")
@Tag(name = "接口测试-接口场景管理")
public class SqlScenarioController {

    @Resource
    SqlScenarioService sqlScenarioService;

    @Resource
    SqlScenarioRunService sqlScenarioRunService;

    @PostMapping("/page")
    @Operation(summary = "接口测试-接口场景管理-场景列表(deleted 状态为 1 时为回收站数据)")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ)
//    @CheckOwner(resourceId = "#request.getProjectId()", resourceType = "project")
    public Pager<List<SqlScenarioDTO>> getPage(@Validated @RequestBody SqlScenarioPageRequest request) {
        Page<Object> page = PageHelper.startPage(request.getCurrent(), request.getPageSize(),
                StringUtils.isNotBlank(request.getSortString("id")) ? request.getSortString("id") : "pos desc, id desc");
        return PageUtils.setPageInfo(page, sqlScenarioService.getScenarioPage(request, true, null));
    }

    @PostMapping("/add")
    @Operation(summary = "接口测试-接口场景管理-创建场景")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_ADD)
//    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = SqlScenarioLogService.class)
//    @SendNotice(taskType = NoticeConstants.TaskType.API_SCENARIO_TASK, event = NoticeConstants.Event.CREATE, target = "#targetClass.addScenarioDTO(#request)", targetClass = ApiScenarioNoticeService.class)
    public SqlScenario add(@Validated @RequestBody SqlScenarioAddRequest request) {
        return sqlScenarioService.add(request, SessionUtils.getUserId());
    }

    @GetMapping("/get/{scenarioId}")
    @Operation(summary = "接口测试-接口场景管理-获取场景详情")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ)
//    @CheckOwner(resourceId = "#scenarioId", resourceType = "api_scenario")
    public SqlScenarioDetailDTO getApiScenarioDetailDTO(@PathVariable String scenarioId) {
        return sqlScenarioService.getSqlScenarioDetailDTO(scenarioId, SessionUtils.getUserId());
    }


    /**
     * run方法适用已经保存过的场景用例执行，如果刚刚创建还未保存的场景用例，执行应该调用debug接口
     * @param request
     * @return
     */
    @PostMapping("/run")
    @Operation(summary = "接口测试-接口场景管理-场景执行")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_EXECUTE)
    public TaskRequestDTO run(@Validated @RequestBody SqlScenarioDebugRequest request) {
        return sqlScenarioRunService.run(request, SessionUtils.getUserId());
    }

    @GetMapping("/step/get/{stepId}")
    @Operation(summary = "接口测试-接口场景管理-获取场景步骤详情")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ)
//    @CheckOwner(resourceId = "#stepId", resourceType = "api_scenario_step")
    public Object getStepDetail(@PathVariable String stepId) {
        return JSON.parseObject(JSON.toJSONString(sqlScenarioService.getStepDetail(stepId)));
    }

}
