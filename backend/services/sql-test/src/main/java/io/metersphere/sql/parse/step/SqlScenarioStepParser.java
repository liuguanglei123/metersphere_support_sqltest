package io.metersphere.sql.parse.step;

import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.sdk.util.CommonBeanFactory;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.domain.SqlScenarioBlob;
import io.metersphere.sql.mapper.SqlScenarioBlobMapper;
import io.metersphere.sql.pojo.dto.scenario.ScenarioConfig;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepCommonDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepDetailRequest;
import io.metersphere.sql.pojo.request.SqlMsScenario;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: jianxing
 * @CreateTime: 2024-01-20  15:43
 */
public class SqlScenarioStepParser extends StepParser {
    @Override
    public SqlAbstractMsTestElement parseTestElement(SqlScenarioStepCommonDTO step, String resourceBlob, String stepDetail) {
        SqlMsScenario msScenario = new SqlMsScenario();
        msScenario.setRefType(step.getRefType());
        if (isRef(step.getRefType())) {
            if (StringUtils.isNotBlank(resourceBlob)) {
                msScenario.setScenarioConfig(JSON.parseObject(resourceBlob, ScenarioConfig.class));
            }
        } else {
            if (StringUtils.isNotBlank(stepDetail)) {
                msScenario.setScenarioConfig(JSON.parseObject(stepDetail, ScenarioConfig.class));
            }
        }
        return msScenario;
    }

    /**
     * 获取场景配置详情
     * @param step
     * @return
     */
    @Override
    public Object parseDetail(SqlScenarioStepDetailRequest step) {
        if (isRef(step.getRefType())) {
            SqlScenarioBlobMapper sqlScenarioBlobMapper = CommonBeanFactory.getBean(SqlScenarioBlobMapper.class);
            SqlScenarioBlob sqlScenarioBlob = sqlScenarioBlobMapper.selectByPrimaryKey(step.getResourceId());
            if (sqlScenarioBlob == null || sqlScenarioBlob.getConfig() == null) {
                return null;
            }
            return JSON.parseObject(new String(sqlScenarioBlob.getConfig()), ScenarioConfig.class);
        } else {
            String stepDetailStr= getStepBlobString(step.getId());
            if (StringUtils.isBlank(stepDetailStr)) {
                return null;
            }
            return JSON.parseObject(stepDetailStr, ScenarioConfig.class);
        }
    }
}
