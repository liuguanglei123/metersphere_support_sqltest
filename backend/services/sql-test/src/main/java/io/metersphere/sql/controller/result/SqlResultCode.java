package io.metersphere.sql.controller.result;

import io.metersphere.sdk.exception.IResultCode;

/**
 * @author jianxing
 */
public enum SqlResultCode implements IResultCode {

    SQL_DEBUG_EXIST(104001, "sql_debug_exist"),
    SQL_DEFINITION_EXIST(104002, "sql_definition_exist"),
    SQL_DEFINITION_NOT_EXIST(104003, "resource_not_exist"),
    SQL_DEFINITION_MODULE_NOT_EXIST(10404, "resource_not_exist"),
    RESOURCE_POOL_EXECUTE_ERROR(104005, "resource_pool_execute_error"),
    EXECUTE_RESOURCE_POOL_NOT_CONFIG(104006, "execute_resource_pool_not_config_error"),
    SQL_DEFINITION_MOCK_EXIST(104007, "sql_definition_mock_exist"),
    SQL_SCENARIO_EXIST(104008, "sql_scenario_exist"),
    SQL_RESPONSE_NAME_CODE_UNIQUE(104009, "sql_response_name_code_unique"),
    SQL_SCENARIO_CIRCULAR_REFERENCE(104010, "sql_scenario_circular_reference_error"),
    TASK_ERROR_MESSAGE_INVALID_RESOURCE_POOL(104011, "task_error_message.invalid_resource_pool"),
    CASE_NOT_EXIST(104012, "task_error_message.case_not_exist"),
    ;


    private final int code;
    private final String message;

    SqlResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return getTranslationMessage(this.message);
    }
}
