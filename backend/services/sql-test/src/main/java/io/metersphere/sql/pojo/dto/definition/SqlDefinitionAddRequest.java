package io.metersphere.sql.pojo.dto.definition;

import io.metersphere.api.domain.ApiDefinitionCustomField;
import io.metersphere.sdk.constants.ModuleConstants;
import io.metersphere.sdk.dto.result.ListResult;
import io.metersphere.sdk.valid.EnumValue;
import io.metersphere.sql.constant.SqlDefinitionStatus;
import io.metersphere.sql.pojo.dto.SqlRequestParams;
import io.metersphere.sql.pojo.vo.ExecuteResultVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static io.metersphere.sdk.constants.ModuleConstants.*;

@Data
public class SqlDefinitionAddRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "SQL用例名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 255, message = "{api_definition.name.length_range}")
    @NotBlank
    private String name;

    // 未来如果支持pg协议等，可能会用到此字段，现在还用不到，取默认值即可
    @Schema(description = "SQL用例协议", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_definition.protocol.not_blank}")
    @Size(min = 1, max = 20, message = "{api_definition.protocol.length_range}")
    private String sqlProtocol = "MYSQL";

    @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_definition.project_id.not_blank}")
    @Size(min = 1, max = 50, message = "{api_definition.project_id.length_range}")
    private String projectId;

    @Schema(description = "SQL语句类型，比如DDL DQL DML等")
    @Size(max = 20, message = "{api_debug.method.length_range}")
    private String sqlMethod;

    @Schema(description = "接口状态/进行中/已完成", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_definition.status.not_blank}")
    @Size(min = 1, max = 50, message = "{api_definition.status.length_range}")
    @EnumValue(enumClass = SqlDefinitionStatus.class)
    private String status;

    @Schema(description = "模块fk", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{api_definition.module_id.not_blank}")
    @Size(min = 1, max = 50, message = "{api_definition.module_id.length_range}")
    private String moduleId;

    @Schema(description = "版本fk")
    @Size(max = 50, message = "{api_definition.version_id.length_range}")
    private String versionId;

    @Schema(description = "描述")
    @Size(max = 1000, message = "{api_definition.description.length_range}")
    private String description;

    @Schema(description = "标签")
    private LinkedHashSet<
            @NotBlank
            @Size(min = 1, max = 64, message = "{api_test_case.tag.length_range}")
                    String> tags;

    @Schema(description = "请求内容")
    @NotNull
    private Object request;

    @Schema(description = "响应内容定义")
    @NotNull
    @Valid
    private ListResult<ExecuteResultVO> response;

    // TODO：
//    @Schema(description = "自定义字段集合")
//    private List<ApiDefinitionCustomField> customFields;

     // TODO：
//    public List<String> getTags() {
//        if (tags == null) {
//            return new ArrayList<>(0);
//        } else {
//            return new ArrayList<>(tags);
//        }
//    }

}
