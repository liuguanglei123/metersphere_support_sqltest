package io.metersphere.sql.controller.scenario;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sql.pojo.dto.report.SqlReportPageRequest;
import io.metersphere.sql.pojo.dto.report.SqlScenarioReportListDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioReportDTO;
import io.metersphere.sql.service.scenario.SqlScenarioReportService;
import io.metersphere.system.security.CheckOwner;
import io.metersphere.system.utils.PageUtils;
import io.metersphere.system.utils.Pager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sql/report/scenario")
@Tag(name = "接口测试-接口报告-场景")
public class SqlScenarioReportController {

    @Resource
    private SqlScenarioReportService sqlScenarioReportService;

    @PostMapping("/page")
    @Operation(summary = "接口测试-接口报告-场景()")
    @CheckOwner(resourceId = "#request.getProjectId()", resourceType = "project")
    @RequiresPermissions(PermissionConstants.PROJECT_API_REPORT_READ)
    public Pager<List<SqlScenarioReportListDTO>> getPage(@Validated @RequestBody SqlReportPageRequest request) {
        Page<Object> page = PageHelper.startPage(request.getCurrent(), request.getPageSize(),
                StringUtils.isNotBlank(request.getSortString()) ? request.getSortString() : "start_time desc");
        return PageUtils.setPageInfo(page, sqlScenarioReportService.getPage(request));
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "接口测试-接口报告-报告获取")
//    @CheckOwner(resourceId = "#id", resourceType = "api_scenario_report")
//    @RequiresPermissions(value = {PermissionConstants.PROJECT_API_REPORT_READ, PermissionConstants.PROJECT_API_SCENARIO_EXECUTE}, logical = Logical.OR)
    public SqlScenarioReportDTO get(@PathVariable String id) {
        return sqlScenarioReportService.get(id);
    }

}
