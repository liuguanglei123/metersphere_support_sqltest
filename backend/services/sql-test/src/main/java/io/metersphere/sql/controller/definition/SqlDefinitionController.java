package io.metersphere.sql.controller.definition;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.api.domain.ApiDefinition;
import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sdk.dto.result.ListResult;
import io.metersphere.sql.aspect.ConnectionInfoAspect;
import io.metersphere.sql.converter.RdbWebConverter;
import io.metersphere.sql.domain.SqlDefinition;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionAddRequest;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionDTO;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionPageRequest;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionRunRequest;
import io.metersphere.sql.pojo.params.DlExecuteParam;
import io.metersphere.sql.service.common.DlTemplateService;
import io.metersphere.sql.service.definition.SqlDefinitionLogService;
import io.metersphere.sql.service.definition.SqlDefinitionNoticeService;
import io.metersphere.sql.service.definition.SqlDefinitionService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sql/definition")
@Tag(name = "SQL测试-SQL CASE定义")
@ConnectionInfoAspect
public class SqlDefinitionController {
    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DlTemplateService dlTemplateService;

    @Resource
    private SqlDefinitionService sqlDefinitionService;

    @PostMapping("/debug")
    @Operation(summary = "SQL定义--SQL调试")
    // TODO:所有的权限部分需要重新定义
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEFINITION_EXECUTE)
    public ListResult<Void> debug(@Validated @RequestBody SqlDefinitionRunRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        dlTemplateService.execute(param);
        return ListResult.empty();
    }

    // TODO：需要测试一下Log和notice的注解功能，目前还不太明确
    @PostMapping(value = "/add")
    @Operation(summary = "SQL测试-SQL定义-添加SQL用例")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEFINITION_ADD)
    @Log(type = OperationLogType.ADD, expression = "#msClass.addLog(#request)", msClass = SqlDefinitionLogService.class)
    @CheckOwner(resourceId = "#request.getProjectId()", resourceType = "project")
    @SendNotice(taskType = NoticeConstants.TaskType.API_DEFINITION_TASK, event = NoticeConstants.Event.CREATE, target = "#targetClass.getApiDTO(#request)", targetClass = SqlDefinitionNoticeService.class)
    public SqlDefinition add(@Validated @RequestBody SqlDefinitionAddRequest request) {
        return sqlDefinitionService.create(request, SessionUtils.getUserId());
    }

    @GetMapping("/blobs/{id}")
    public List<String> getBlobsAsZip(@PathVariable String id) {
        return sqlDefinitionService.getrequestAndResp(id);
    }

    @PostMapping("/page")
    @Operation(summary = "SQL测试-SQL用例管理-SQL列表(deleted 状态为 1 时为回收站数据)")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEFINITION_READ)
    @CheckOwner(resourceId = "#request.getProjectId()", resourceType = "project")
    public Pager<List<SqlDefinitionDTO>> getPage(@Validated @RequestBody SqlDefinitionPageRequest request) {
        // 暂不清楚作用，先删除
//        sqlDefinitionService.initApiSelectIds(request);
        Page<Object> page = PageHelper.startPage(request.getCurrent(), request.getPageSize(),
                StringUtils.isNotBlank(request.getSortString("id")) ? request.getSortString("id") : request.getDeleted() ? "delete_time desc, id desc" : "pos desc, id desc");
        return PageUtils.setPageInfo(page, sqlDefinitionService.getApiDefinitionPage(request, SessionUtils.getUserId()));
    }

    // TODO：回收站功能没做，后续可以根据实际情况加一下
    @GetMapping("/delete-to-gc/{id}")
    @Operation(summary = "SQL测试-SQL用例管理-删除SQL用例到回收站")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEFINITION_DELETE)
    @Log(type = OperationLogType.DELETE, expression = "#msClass.moveToGcLog(#id)", msClass = SqlDefinitionLogService.class)
// TODO：权限检查   @CheckOwner(resourceId = "#id", resourceType = "sql_definition")
    @SendNotice(taskType = NoticeConstants.TaskType.SQL_DEFINITION_TASK, event = NoticeConstants.Event.DELETE, target = "#targetClass.getDeleteSqlDTO(#id)", targetClass = SqlDefinitionNoticeService.class)
    public void deleteToGc(@PathVariable String id, @RequestParam(required = false) boolean deleteAllVersion) {
        sqlDefinitionService.deleteToGc(id, deleteAllVersion, SessionUtils.getUserId());
    }

    @GetMapping(value = "/get-detail/{id}")
    @Operation(summary = "SQL测试-SQL用例管理-获取用例详情")
    @RequiresPermissions(PermissionConstants.PROJECT_API_DEFINITION_READ)
// TODO：权限检查    @CheckOwner(resourceId = "#id", resourceType = "api_definition")
    public SqlDefinitionDTO get(@PathVariable String id) {
        return sqlDefinitionService.get(id, SessionUtils.getUserId());
    }
}
