package io.metersphere.sql.constant;

public enum SqlScenarioStepType {
    /**
     * 接口定义
     */
    SQL(SqlStepTypeGroup.REQUEST),
//    TODO：/**
//     * 接口用例
//     */
//    SQL_SCENARIO_STEP_TYPE_CASE(StepTypeGroup.REQUEST),
//    /**
//     * 自定义请求
//     */
//    CUSTOM_REQUEST(StepTypeGroup.REQUEST),
    /**
     * 场景
     */
    SQL_SCENARIO(SqlStepTypeGroup.SCENARIO);
//    /**
//     * 循环控制器
//     */
//    LOOP_CONTROLLER(StepTypeGroup.CONTROLLER),
//    /**
//     * 条件控制器
//     */
//    IF_CONTROLLER(StepTypeGroup.CONTROLLER),
//    /**
//     * 一次控制器
//     */
//    ONCE_ONLY_CONTROLLER(StepTypeGroup.CONTROLLER),
//    /**
//     * 等待控制器
//     */
//    CONSTANT_TIMER(StepTypeGroup.REQUEST),
//    /**
//     * 脚本操作
//     */
//    SCRIPT(StepTypeGroup.REQUEST),
//
//    /**
//     * JMeter插件
//     */
//    JMETER_COMPONENT(StepTypeGroup.REQUEST);


    private enum SqlStepTypeGroup {
        REQUEST, CONTROLLER, SCENARIO
    }

    private SqlStepTypeGroup stepTypeGroup;

    SqlScenarioStepType(SqlStepTypeGroup stepTypeGroup) {
        this.stepTypeGroup = stepTypeGroup;
    }

    public Boolean isRequest() {
        return this.stepTypeGroup.equals(SqlStepTypeGroup.REQUEST);
    }
}
