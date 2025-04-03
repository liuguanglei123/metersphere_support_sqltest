package io.metersphere.sql.constant;

public enum SqlScenarioStepRefType {
    /**
     * 在场景中直接创建的步骤
     * 例如 自定义请求，逻辑控制器
     */
    DIRECT,
    /**
     * 完全引用
     */
    REF,
    /**
     * 部分引用
     */
    PARTIAL_REF,
    /**
     * 复制
     */
    COPY
}