package io.metersphere.sql.pojo.dto.scenario;

import lombok.Data;

/**
 * @Author: jianxing
 * @CreateTime: 2024-01-12  09:47
 */
@Data
public class ScenarioConfig {
    private String id;
    // TODO：仅占位，在第一个版本中不支持如下内容，上面的id只是随便放个字段防止序列化的时候报错而已
//    /**
//     * 场景变量
//     */
//    @Valid
//    private ScenarioVariable variable = new ScenarioVariable();
//    /**
//     * 前置处理器配置
//     */
//    private MsProcessorConfig preProcessorConfig = new MsProcessorConfig();
//    /**
//     * 后置处理器配置
//     */
//    private MsProcessorConfig postProcessorConfig = new MsProcessorConfig();
//    /**
//     * 断言配置
//     */
//    private MsScenarioAssertionConfig assertionConfig = new MsScenarioAssertionConfig();
//    /**
//     * 其他配置
//     */
//    private ScenarioOtherConfig otherConfig = new ScenarioOtherConfig();
}
