package io.metersphere.sql.pojo.request;

import io.metersphere.plugin.api.spi.AbstractMsProtocolTestElement;
import io.metersphere.sdk.constants.HttpMethodConstants;
import io.metersphere.sdk.constants.SqlMethodConstants;
import io.metersphere.sdk.valid.EnumValue;
import io.metersphere.sql.pojo.dto.SqlRequestParams;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SQL测试中sql用例的类型标识，当接口中的polymorphicName参数值为类名 MsSqlCaseElement 时，会查找合适的request和response类型进行解析
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MsSqlCaseElement extends AbstractMsProtocolTestElement {
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

}
