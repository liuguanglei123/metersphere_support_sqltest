package io.metersphere.sql.pojo.dto;

import lombok.Data;

/**
 * 执行时需要设置的接口信息
 * @Author: jianxing
 * @CreateTime: 2024-02-17  18:46
 */
@Data
public class SqlDefinitionExecuteInfo {
    /**
     * 模块Id
     */
    private String moduleId;
    /**
     * 资源id，接口定义，接口用例等
     */
    private String resourceId;

}
