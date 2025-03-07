package io.metersphere.sql.converter;


import io.metersphere.sql.pojo.dto.SqlRequestParams;
import io.metersphere.sql.pojo.dto.debug.SqlDebugRunRequest;
import io.metersphere.sql.pojo.model.Command;
import io.metersphere.sql.pojo.params.DlExecuteParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class CommandConverter {

    @Mappings({
            @Mapping(target = "script", source = "request.body.sqlContent")
    })
    public abstract Command param2model(DlExecuteParam param);

    public abstract SqlRequestParams requestParams2params(SqlRequestParams requestParams);

    public abstract SqlRequestParams.SqlExecuteBody SqlExecuteBody2body(SqlRequestParams.SqlExecuteBody requestBody);

}
