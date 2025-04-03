package io.metersphere.plugin.api.spi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * <pre>
 * 该对象传参时，需要传入 polymorphicName 字段，用于区分是哪个协议的组件
 * 对应协议的组件 polymorphicName 字段，调用 /api/test/protocol/{organizationId} 接口获取
 * <pre>
 * @Author: jianxing
 * @CreateTime: 2023-10-30  15:08
 */
@Data
public abstract class SqlAbstractMsTestElement extends AbstractMsTestElement {
    /**
     * 插件类型，用于识别是SqlCase还是SqlScenario
     */
    private String polymorphicName;

    private String reportId;

    /**
     * 子组件
     */
    private LinkedList<SqlAbstractMsTestElement> sqlChildren = new LinkedList<>();
}
