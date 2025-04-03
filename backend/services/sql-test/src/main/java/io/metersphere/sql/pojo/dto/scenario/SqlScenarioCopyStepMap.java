package io.metersphere.sql.pojo.dto.scenario;

import lombok.Data;

import java.util.Map;

@Data
public class SqlScenarioCopyStepMap {

    /**
     * key 的 stepId，value 为 copyFrom 的步骤ID
     */
    private Map<String, String> copyFromStepIdMap;
    /**
     * key 的 stepId，value 为 copyFrom 的接口ID
     */
    private Map<String, String> isNewSqlResourceMap;

}
