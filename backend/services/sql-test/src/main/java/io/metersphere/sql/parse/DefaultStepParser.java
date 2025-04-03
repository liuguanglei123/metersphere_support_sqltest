package io.metersphere.sql.parse;

import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.sql.parse.step.StepParser;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepCommonDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepDetailRequest;

/**
 * 默认的步骤解析器
 * 逻辑控制器等步骤，直接将步骤详情解析为 MsTestElement
 * @Author: jianxing
 * @CreateTime: 2024-01-20  15:43
 */
public class DefaultStepParser extends StepParser {

    @Override
    public SqlAbstractMsTestElement parseTestElement(SqlScenarioStepCommonDTO step, String resourceBlob, String stepDetail) {
        return parse2MsTestElement(stepDetail);
    }

    @Override
    public Object parseDetail(SqlScenarioStepDetailRequest step) {
        return parse2MsTestElement(getStepBlobString(step.getId()));
    }
}
