package io.metersphere.sql.controller;

import io.metersphere.sdk.constants.PermissionConstants;
import io.metersphere.sdk.dto.api.task.TaskRequestDTO;
import io.metersphere.sql.converter.RdbWebConverter;
import io.metersphere.sql.aspect.ConnectionInfoAspect;
import io.metersphere.sql.pojo.dto.debug.SqlDebugRunRequest;
import io.metersphere.sql.pojo.model.ExecuteResult;
import io.metersphere.sql.pojo.params.DlExecuteParam;
import io.metersphere.sql.pojo.request.DmlRequest;
import io.metersphere.sql.pojo.vo.ExecuteResultVO;
import io.metersphere.sql.service.common.DlTemplateService;
import io.metersphere.sql.wrapper.result.ListResult;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@ConnectionInfoAspect
@RequestMapping("/sql/debug")
@RestController
public class SqlTestController {
    @Autowired
    private RdbWebConverter rdbWebConverter;

    @Autowired
    private DlTemplateService dlTemplateService;
    /**
     * 获取当前用户的数据库连接信息，如果数据库连接信息不存在，则根据当前登录用户+user_connection_info表中选中的数据库信息创建对应连接并返回
     * @return 数据库连接信息
     */
    @PostMapping(value="/execute")
    public ListResult<ExecuteResultVO> executeDirect(@RequestBody DmlRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        ListResult<ExecuteResult> resultDTOListResult = dlTemplateService.executeDirect(param);
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



}
