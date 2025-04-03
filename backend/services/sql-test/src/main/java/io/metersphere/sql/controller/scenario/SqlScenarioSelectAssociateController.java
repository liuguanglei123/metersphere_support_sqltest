package io.metersphere.sql.controller.scenario;

import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioSelectAssociateDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepDTO;
import io.metersphere.sql.service.scenario.SqlScenarioSelectAssociateService;
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

@RestController
@RequestMapping(value = "/sql/scenario/associate")
@Tag(name = "SQL测试-SQL场景管理-场景导入系统参数")
public class SqlScenarioSelectAssociateController {
    @Resource
    private SqlScenarioSelectAssociateService sqlScenarioSelectAssociateService;


    @PostMapping("/all")
    @Operation(summary = "SQL场景管理-场景导入系统参数")
    @RequiresPermissions(PermissionConstants.PROJECT_API_SCENARIO_UPDATE)
    public List<SqlScenarioStepDTO> getSelectDto(@Validated @RequestBody Map<String, SqlScenarioSelectAssociateDTO> requestMap) {
        return sqlScenarioSelectAssociateService.getSelectDto(requestMap);
    }
}
