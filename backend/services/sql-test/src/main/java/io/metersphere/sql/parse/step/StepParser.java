package io.metersphere.sql.parse.step;

import io.metersphere.api.domain.ApiScenarioStepBlob;
import io.metersphere.api.mapper.ApiScenarioStepBlobMapper;
import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.sdk.util.CommonBeanFactory;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.constant.SqlScenarioStepRefType;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepCommonDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepDetailRequest;
import org.apache.commons.lang3.StringUtils;
import  io.metersphere.sql.utils.SqlDataUtils;

import java.util.Map;

/**
 * @Author: jianxing
 * @CreateTime: 2024-01-20  15:43
 */
public abstract class StepParser {

    /**
     * 将步骤详情解析为 MsTestElement
     *
     * @param step         步骤
     * @param resourceBlob 关联的资源详情
     * @param stepDetail   步骤详情
     * @return
     */
    public abstract SqlAbstractMsTestElement parseTestElement(SqlScenarioStepCommonDTO step, String resourceBlob, String stepDetail);

    /**
     * 将步骤解析为步骤详情
     * 场景步骤，返回 ScenarioConfig
     * 其余返回 MsTestElement
     *
     * @param step
     * @return
     */
    public abstract Object parseDetail(SqlScenarioStepDetailRequest step);


    protected boolean isRef(String refType) {
        return StringUtils.equalsAny(refType, SqlScenarioStepRefType.REF.name(), SqlScenarioStepRefType.PARTIAL_REF.name());
    }

    protected static SqlAbstractMsTestElement parse2MsTestElement(String blobContent) {
        if (StringUtils.isBlank(blobContent)) {
            return null;
        }
        return SqlDataUtils.parseObject(blobContent, SqlAbstractMsTestElement.class);
    }

    protected String getStepBlobString(String stepId) {
        ApiScenarioStepBlobMapper apiScenarioStepBlobMapper = CommonBeanFactory.getBean(ApiScenarioStepBlobMapper.class);
        ApiScenarioStepBlob apiScenarioStepBlob = apiScenarioStepBlobMapper.selectByPrimaryKey(stepId);
        if (apiScenarioStepBlob == null) {
            return null;
        }
        return new String(apiScenarioStepBlob.getContent());
    }

    public <T extends SqlAbstractMsTestElement> T parseConfig2TestElement(Object config, Class<T> clazz) {
        if (config != null && config instanceof Map confiMap) {
            confiMap.put("polymorphicName", clazz.getSimpleName());
            return JSON.parseObject(JSON.toJSONString(confiMap), clazz);
        }
        return null;
    }
    public <T extends SqlAbstractMsTestElement> T parseConfig2TestElement(SqlScenarioStepCommonDTO step, Class<T> clazz) {
        SqlAbstractMsTestElement testElement = parseConfig2TestElement(step.getConfig(), clazz);
        if (testElement == null) {
            return null;
        }
        testElement.setName(step.getName());
        testElement.setEnable(step.getEnable());
        testElement.setStepId(step.getId());
        testElement.setProjectId(step.getProjectId());
        return (T) testElement;
    }
}
