package io.metersphere.sdk.constants;

public class KafkaTopicConstants {
    public static final String PLUGIN = "PLUGIN";
    public static final String EXPORT = "EXPORT";
    // API TOPIC
    public static final String API_REPORT_TOPIC = "API_REPORT_TOPIC";
    public static final String API_REPORT_TASK_TOPIC = "API_REPORT_TASK_TOPIC";
    public static final String API_REPORT_DEBUG_TOPIC = "API_REPORT_DEBUG_TOPIC";

    // SQL TOPIC
    public static final String SQL_REPORT_TOPIC = "SQL_REPORT_TOPIC";
    public static final String SQL_REPORT_TASK_TOPIC = "SQL_REPORT_TASK_TOPIC";
    public static final String SQL_REPORT_DEBUG_TOPIC = "SQL_REPORT_DEBUG_TOPIC";

    // SQL执行的task topic，当前端发起执行SQL时，会将SQL的基本信息写入到该Topic，然后通过sql_executor进行消费（真正执行SQL）
    public static final String SQL_REPORT_DEBUG_TASK_TOPIC = "SQL_REPORT_DEBUG_TASK_TOPIC";
    public static final String SQL_REPORT_DEBUG_TASK_RESULT_TOPIC = "SQL_REPORT_DEBUG_TASK_RESULT_TOPIC";

    public static class TYPE {
        public static final String ADD = "ADD";
        public static final String DELETE = "DELETE";
    }
}
