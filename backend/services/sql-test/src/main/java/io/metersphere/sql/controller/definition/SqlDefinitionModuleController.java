package io.metersphere.sql.controller.definition;

import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sql.pojo.dto.ModuleCreateRequest;
import io.metersphere.sql.pojo.dto.definition.SqlModuleRequest;
import io.metersphere.sql.service.definition.SqlDefinitionModuleService;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import io.metersphere.system.security.CheckOwner;
import io.metersphere.system.utils.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SQL测试-SQL用例管理-模块")
@RestController
@RequestMapping("/sql/definition/module")
public class SqlDefinitionModuleController {

    @Resource
    private SqlDefinitionModuleService sqlDefinitionModuleService;

    @PostMapping("/tree")
    @Operation(summary = "SQL测试-SQL用例管理-模块-目录树-支持搜索")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEFINITION_READ)
    @CheckOwner(resourceId = "#request.projectId", resourceType = "project")
    public List<BaseTreeNode> getTreeAndRequest(@RequestBody @Validated SqlModuleRequest request) {
        return sqlDefinitionModuleService.getTree(request, false, true);
    }

    @PostMapping("/only/tree")
    @Operation(summary = "接口测试-接口管理-模块-不包含请求数据的模块树")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEFINITION_READ)
    @CheckOwner(resourceId = "#request.projectId", resourceType = "project")
    public List<BaseTreeNode> getTree(@RequestBody @Validated SqlModuleRequest request) {
        return sqlDefinitionModuleService.getTree(request, false, false);
    }

    @PostMapping("/add")
    @Operation(summary = "接口测试-接口管理-模块-添加模块")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEFINITION_ADD)
    public String add(@RequestBody @Validated ModuleCreateRequest request) {
        return sqlDefinitionModuleService.add(request, SessionUtils.getUserId());
    }
}
