package io.metersphere.sql.pojo.dto.debug;

import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.sql.domain.SqlDebug;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SqlDebugDTO extends SqlDebug {
    @Schema(description = "请求内容")
    private AbstractMsTestElement request;

    @Schema(description = "响应内容")
    private String response;
}

