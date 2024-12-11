package io.metersphere.sqlExecutor.converter;


import io.metersphere.sqlExecutor.pojo.model.Command;
import io.metersphere.sqlExecutor.pojo.params.DlExecuteParam;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class CommandConverter {

    @Mappings({
            @Mapping(target = "script", source = "sql")
    })
    public abstract Command param2model(DlExecuteParam param);
}
