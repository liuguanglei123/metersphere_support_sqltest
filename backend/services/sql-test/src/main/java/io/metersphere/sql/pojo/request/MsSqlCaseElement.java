package io.metersphere.sql.pojo.request;

import io.metersphere.plugin.api.spi.AbstractMsProtocolTestElement;
import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.sdk.constants.HttpMethodConstants;
import io.metersphere.sdk.dto.result.ListResult;
import io.metersphere.sql.pojo.dto.SqlRequestParams;
import io.metersphere.sql.pojo.vo.ExecuteResultVO;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * SQL测试中sql用例的类型标识，当接口中的polymorphicName参数值为类名 MsSqlCaseElement 时，会查找合适的request和response类型进行解析
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MsSqlCaseElement extends SqlAbstractMsTestElement {
    /**
     * 请求方法
     * 取值参考：{@link HttpMethodConstants}
     */
//    @NotBlank
//    @EnumValue(enumClass = SqlMethodConstants.class)
//    private String sqlMethod;
    /**
     * 请求体
     */
    @Valid
    private SqlRequestParams.SqlExecuteBody body;
    /**
     * 模块ID
     * 运行时参数，接口无需设置
     */
    private String moduleId;

    private ListResult<ExecuteResultVO> responseDefinition;

}
