package io.metersphere.sql.constant;

public class SqlCoverageConstants {

    public static final String COVER_FROM = "coverFrom";
    public static final String UN_COVER_FROM = "unCoverFrom";

    // TODO：目前应该只有SQL_CASE这一种选项，在sql的测试中相当于api测试的definition+case，至于这里为什么保留了场景选项，是为了以后扩展考虑，如果确定无扩展可以考虑删除
//    public static final String SQL_DEFINITION = "sqlDefinition";
    public static final String SQL_CASE = "sqlCase";
    public static final String SQL_SCENARIO = "sqlScenario";
}
