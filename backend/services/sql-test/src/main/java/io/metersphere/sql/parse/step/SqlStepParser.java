package io.metersphere.sql.parse.step;

import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.sdk.util.CommonBeanFactory;
import io.metersphere.sql.domain.SqlDefinitionBlob;
import io.metersphere.sql.mapper.SqlDefinitionBlobMapper;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepDetailRequest;
import io.metersphere.sql.pojo.request.MsSqlCaseElement;
import io.metersphere.sql.utils.SqlDataUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepCommonDTO;
import java.util.List;

/**
 * @Author: jianxing
 * @CreateTime: 2024-01-20  15:43
 */
public class SqlStepParser extends StepParser {
    @Override
    public SqlAbstractMsTestElement parseTestElement(SqlScenarioStepCommonDTO step, String resourceBlob, String stepDetail) {
        if (isRef(step.getRefType())) {
            return parseRefTestElement(resourceBlob, stepDetail);
        } else {
            return StringUtils.isBlank(stepDetail) ? null : SqlDataUtils.parseObject(stepDetail, SqlAbstractMsTestElement.class);
        }
    }

    /**
     * 处理引用的接口步骤
     * 替换修改的参数
     * @param resourceBlob 引用的接口步骤详情
     * @param stepDetail 引用之后修改的步骤详情
     * @return
     */
    public SqlAbstractMsTestElement parseRefTestElement(String resourceBlob, String stepDetail) {
        if (StringUtils.isBlank(resourceBlob)) {
            return null;
        }
        SqlAbstractMsTestElement refResourceElement = parse2MsTestElement(resourceBlob);
        if (refResourceElement instanceof MsSqlCaseElement && StringUtils.isNotBlank(stepDetail)) {
            // 如果是 http 并且有修改请求参数，则替换请求参数
            SqlAbstractMsTestElement stepElement = parse2MsTestElement(stepDetail);
            return replaceParams((MsSqlCaseElement) stepElement, (MsSqlCaseElement) refResourceElement);
        } else {
            return refResourceElement;
        }
    }

    @Override
    public Object parseDetail(SqlScenarioStepDetailRequest step) {
        if (isRef(step.getRefType())) {
            SqlDefinitionBlobMapper sqlDefinitionBlobMapper = CommonBeanFactory.getBean(SqlDefinitionBlobMapper.class);
            SqlDefinitionBlob apiDefinitionBlob = sqlDefinitionBlobMapper.selectByPrimaryKey(step.getResourceId());
            if (apiDefinitionBlob == null) {
                return null;
            }
            return parseRefTestElement(new String(apiDefinitionBlob.getRequest()), getStepBlobString(step.getId()));
        } else {
            return parse2MsTestElement(getStepBlobString(step.getId()));
        }
    }


    private SqlAbstractMsTestElement replaceParams(MsSqlCaseElement msTestElement, MsSqlCaseElement refResourceElement) {
// TODO：替换请求体中的参数       replaceBodyParams(msTestElement.getBody(), refResourceElement.getBody());
        return refResourceElement;
    }

    /**
     * 替换请求体中的参数
     *
     * @param valueBody
     * @param refBody
     */
//    private void replaceBodyParams(Body valueBody, Body refBody) {
//        if (refBody == null || valueBody == null) {
//            return;
//        }
//        if (valueBody.getFormDataBody() != null && refBody.getFormDataBody() != null) {
//            replaceKvParam(valueBody.getFormDataBody().getFormValues(), refBody.getFormDataBody().getFormValues());
//        }
//        if (valueBody.getWwwFormBody() != null && refBody.getWwwFormBody() != null) {
//            replaceKvParam(valueBody.getWwwFormBody().getFormValues(), refBody.getWwwFormBody().getFormValues());
//        }
//        if (valueBody.getBinaryBody() != null && refBody.getBinaryBody() != null) {
//            refBody.getBinaryBody().setFile(valueBody.getBinaryBody().getFile());
//        }
        // todo JsonSchema body
//    }

    /**
     * TODO:替换参数
     * 暂不需要
     *
     * @param valueList
     * @param refList
     */
//    private void replaceKvParam(List valueList, List refList) {
//        if (CollectionUtils.isEmpty(refList) || CollectionUtils.isEmpty(valueList)) {
//            return;
//        }
//        refList.forEach(item -> {
//            KeyValueParam refParam = (KeyValueParam) item;
//            for (Object valueItem : valueList) {
//                KeyValueParam valueParam = (KeyValueParam) valueItem;
//                if (StringUtils.equals(refParam.getKey(), valueParam.getKey())) {
//                    refParam.setValue(valueParam.getValue());
//                    if (refParam instanceof FormDataKV refFormDataKey && valueParam instanceof FormDataKV valueFormDataKey) {
//                        refFormDataKey.setFiles(valueFormDataKey.getFiles());
//                    }
//                    break;
//                }
//            }
//        });
//    }
}
