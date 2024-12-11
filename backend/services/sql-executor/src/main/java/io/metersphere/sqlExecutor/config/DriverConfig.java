
package io.metersphere.sqlExecutor.config;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author jipengfei
 * @version : DriverConfig.java
 */
@Data
public class DriverConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * url
     */
    private String url;
    /**
     * jdbcDriver
     */
    private String jdbcDriver;

    /**
     * jdbcDriverClass
     */
    private String jdbcDriverClass;

    /**
     * downloadJdbcDriverUrls
     */
    private List<String> downloadJdbcDriverUrls;

    /**
     * dbType
     */
    private String dbType;

    /**
     * customize
     */
    private boolean custom;

    /**
     * properties
     */
//    private List<KeyValue> extendInfo;


    private boolean defaultDriver;

    public boolean notEmpty() {
       return StringUtils.isNotBlank(getJdbcDriver()) && StringUtils.isNotBlank(
            getJdbcDriverClass());
    }
}