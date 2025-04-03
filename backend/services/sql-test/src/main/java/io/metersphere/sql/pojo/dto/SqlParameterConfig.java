package io.metersphere.sql.pojo.dto;

import lombok.Data;

@Data
public abstract class SqlParameterConfig {
    /**
     * 任务项的唯一ID
     */
    private String taskItemId;
    /**
     * 报告ID
     */
    private String reportId;
}
