package io.metersphere.sqlExecutor.converter;

import io.metersphere.sqlExecutor.pojo.request.DmlRequest;
import io.metersphere.sqlExecutor.pojo.params.DlExecuteParam;
import org.mapstruct.Mapper;

/**
 * @author moji
 * @version MysqlDataConverter.java, v 0.1 October 14, 2022 14:04 moji Exp $
 * @date 2022/10/14
 */
@Mapper(componentModel = "spring")
public abstract class RdbWebConverter {

    /**
     * Parameter conversion
     *
     * @param request
     * @return
     */
    public abstract DlExecuteParam request2param(DmlRequest request);

}
