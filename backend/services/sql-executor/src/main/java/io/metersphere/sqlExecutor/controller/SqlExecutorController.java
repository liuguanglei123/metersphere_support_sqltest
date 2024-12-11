package io.metersphere.sqlExecutor.controller;

import io.metersphere.sqlExecutor.pojo.request.DmlRequest;
import io.metersphere.sqlExecutor.pojo.vo.ExecuteResultVO;
import io.metersphere.sqlExecutor.aspect.ConnectionInfoAspect;
import io.metersphere.sqlExecutor.converter.RdbWebConverter;
import io.metersphere.sqlExecutor.pojo.model.ExecuteResult;
import io.metersphere.sqlExecutor.pojo.params.DlExecuteParam;
import io.metersphere.sqlExecutor.wrapper.result.ListResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@ConnectionInfoAspect
@RequestMapping("/api/sql/statement")
@RestController
public class SqlExecutorController {

    @Autowired
    private RdbWebConverter rdbWebConverter;

    /**
     * Data operation and maintenance such as addition, deletion, modification and query
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/execute", method = {RequestMethod.POST, RequestMethod.PUT})
    public ListResult<ExecuteResultVO> manage(@RequestBody DmlRequest request) {
        DlExecuteParam param = rdbWebConverter.request2param(request);
        ListResult<ExecuteResult> resultDTOListResult = dlTemplateService.execute(param);
        List<ExecuteResultVO> resultVOS = rdbWebConverter.dto2vo(resultDTOListResult.getData());
        return ListResult.of(resultVOS);
    }
}
