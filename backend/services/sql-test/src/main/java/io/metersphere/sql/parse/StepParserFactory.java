package io.metersphere.sql.parse;

import io.metersphere.sql.constant.SqlScenarioStepType;
import io.metersphere.sql.parse.step.SqlScenarioStepParser;
import io.metersphere.sql.parse.step.SqlStepParser;
import io.metersphere.sql.parse.step.StepParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: jianxing
 * @CreateTime: 2024-01-20  15:43
 */
public abstract class StepParserFactory {

    private static Map<String, StepParser> stepParserMap = new HashMap<>();
    private static StepParser defaultStepParser = new DefaultStepParser();

    static {
        stepParserMap.put(SqlScenarioStepType.SQL.name(), new SqlStepParser());
        stepParserMap.put(SqlScenarioStepType.SQL_SCENARIO.name(), new SqlScenarioStepParser());

    }

    public static StepParser getStepParser(String stepType) {
        StepParser stepParser = stepParserMap.get(stepType);
        return stepParser == null ? defaultStepParser : stepParser;
    }
}
