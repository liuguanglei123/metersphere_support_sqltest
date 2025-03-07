package io.metersphere.sql.converter;

import io.metersphere.sql.pojo.dto.SqlRequestParams;
import io.metersphere.sql.pojo.dto.debug.SqlDebugRunRequest;
import io.metersphere.sql.pojo.model.ExecuteResult;
import io.metersphere.sql.pojo.request.DmlRequest;
import io.metersphere.sql.pojo.params.DlExecuteParam;
import io.metersphere.sql.pojo.vo.ExecuteResultVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class RdbWebConverter {

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract DlExecuteParam request2param(DmlRequest request);

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract DlExecuteParam request2param(SqlDebugRunRequest request);

    /**
     * Model conversion
     *
     * @param dtos
     * @return
     */
    public abstract List<ExecuteResultVO> dto2vo(List<ExecuteResult> dtos);

    public abstract SqlRequestParams requestParams2params(SqlRequestParams requestParams);

    public abstract SqlRequestParams.SqlExecuteBody SqlExecuteBody2body(SqlRequestParams.SqlExecuteBody requestBody);


}
