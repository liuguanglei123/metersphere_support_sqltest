package io.metersphere.sql.controller.data.source.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 关于如何在请求中带出连接的数据库的信息，最终采用和chat2db一样的方式，使用DataSourceBaseRequest作为基类，所有关于数据库请求的body都集成此类
 * 该类的作用是在请求中带出数据库连接id，该id最终用于在 ConnectionInfoHandler 切面类中获取数据库连接信息
 */
@Data
public class DataSourceBaseRequest{

    /**
     * user-connection-info表中的id值
     */
    @NotNull
    private Long dataSourceId;

    /**
     * DB name
     */
    private String databaseName;

    /**
     * The space where the table is located
     */
    private String schemaName;

    /**
     * if true, refresh the cache
     */
    private boolean refresh;

}
