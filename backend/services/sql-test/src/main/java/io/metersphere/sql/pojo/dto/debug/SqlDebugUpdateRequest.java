package io.metersphere.sql.pojo.dto.debug;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SqlDebugUpdateRequest {
    private static final long serialVersionUID = 1L;

    @Schema(description = "接口pk", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_debug.id.not_blank}")
    @Size(max = 50, message = "{api_debug.id.length_range}")
    private String id;

    @Schema(description = "接口名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 255, message = "{api_debug.name.length_range}")
    private String name;

    @Schema(description = "模块fk", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "{api_debug.module_id.length_range}")
    private String moduleId;

    @Schema(description = "请求内容")
    private Object request;
}
