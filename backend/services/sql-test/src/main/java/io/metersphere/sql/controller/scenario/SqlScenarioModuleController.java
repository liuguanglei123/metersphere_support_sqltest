package io.metersphere.sql.controller.scenario;

import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sql.pojo.dto.ModuleCreateRequest;
import io.metersphere.sql.pojo.dto.ModuleUpdateRequest;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioModuleRequest;
import io.metersphere.sql.service.scenario.SqlScenarioModuleService;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import io.metersphere.system.security.CheckOwner;
import io.metersphere.system.utils.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "SQL测试-SQL执行-模块")
@RestController
@RequestMapping("/sql/scenario/module")
public class SqlScenarioModuleController {

    @Resource
    private SqlScenarioModuleService sqlScenarioModuleService;

    @PostMapping("/tree")
    @Operation(summary = "SQL测试-SQL执行-模块-查找模块")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ)
    @CheckOwner(resourceId = "#request.projectId", resourceType = "project")
    public List<BaseTreeNode> getTree(@RequestBody @Validated SqlScenarioModuleRequest request) {
        return sqlScenarioModuleService.getTree(request);
    }


    @PostMapping("/count")
    @Operation(summary = "SQL测试-SQL执行-模块-统计模块数量")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_READ)
    @CheckOwner(resourceId = "#request.projectId", resourceType = "project")
    public Map<String, Long> moduleCount(@Validated @RequestBody SqlScenarioModuleRequest request) {
        return sqlScenarioModuleService.moduleCount(request, false);
    }

    @PostMapping("/add")
    @Operation(summary = "SQL测试-SQL执行-模块-添加模块")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_ADD)
    public String add(@RequestBody @Validated ModuleCreateRequest request) {
        return sqlScenarioModuleService.add(request, SessionUtils.getUserId());
    }

    @PostMapping("/update")
    @Operation(summary = "SQL测试-SQL执行-模块-修改模块")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_UPDATE)
//    @CheckOwner(resourceId = "#request.id", resourceType = "api_scenario_module")
    public boolean update(@RequestBody @Validated ModuleUpdateRequest request) {
        sqlScenarioModuleService.update(request, SessionUtils.getUserId());
        return true;
    }

}
