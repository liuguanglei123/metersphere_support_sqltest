package io.metersphere.sql.controller.debug;

import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sql.pojo.dto.ModuleCreateRequest;
import io.metersphere.sql.pojo.dto.ModuleUpdateRequest;
import io.metersphere.sql.pojo.dto.SqlDebugRequest;
import io.metersphere.sql.service.debug.SqlDebugModuleService;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import io.metersphere.system.dto.sdk.request.NodeMoveRequest;
import io.metersphere.system.utils.SessionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "SQL测试-SQL调试-模块")
@RestController
@RequestMapping("/sql/debug/module")
public class SqlDebugModuleController {

    @Resource
    private SqlDebugModuleService sqlDebugModuleService;

    @GetMapping("/tree")
    @Operation(summary = "SQL测试-SQL调试-模块-展示模块")
    //TODO：所有的权限部分，都需要重新调整
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_READ)
    public List<BaseTreeNode> getTree() {
        return sqlDebugModuleService.getTree(SessionUtils.getUserId());
    }

    @PostMapping("/count")
    @Operation(summary = "SQL测试-SQL调试-模块-统计模块数量")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_READ)
    public Map<String, Long> moduleCount(@Validated @RequestBody SqlDebugRequest request) {
        return sqlDebugModuleService.moduleCount(request, SessionUtils.getUserId());
    }

    @PostMapping("/add")
    @Operation(summary = "SQL测试-SQL调试-模块-添加模块")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_ADD)
    public String add(@RequestBody @Validated ModuleCreateRequest request) {
        return sqlDebugModuleService.add(request, SessionUtils.getUserId());
    }
    @PostMapping("/update")
    @Operation(summary = "接口测试-接口调试-模块-修改模块")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_UPDATE)
    public boolean update(@RequestBody @Validated ModuleUpdateRequest request) {
        sqlDebugModuleService.update(request, SessionUtils.getUserId(), SessionUtils.getCurrentProjectId());
        return true;
    }

    @GetMapping("/delete/{deleteId}")
    @Operation(summary = "接口测试-接口调试-模块-删除模块")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_DELETE)
    public void deleteNode(@PathVariable String deleteId) {
        sqlDebugModuleService.deleteModule(deleteId, SessionUtils.getUserId());
    }

    @PostMapping("/move")
    @Operation(summary = "接口测试-接口调试-模块-移动模块")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEBUG_UPDATE)
    public void moveNode(@Validated @RequestBody NodeMoveRequest request) {
        sqlDebugModuleService.moveNode(request, SessionUtils.getUserId());
    }

}
