package io.metersphere.sqlExecutor.pojo.request;

import io.metersphere.sqlExecutor.controller.data.source.request.DataSourceBaseRequest;
import io.metersphere.sqlExecutor.controller.data.source.request.DataSourceConsoleRequestInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author moji
 * @version TableManageRequest.java, v 0.1 September 16, 2022 17:55 moji Exp $
 * @date 2022/09/16
 */
@Data
public class DmlRequest extends DataSourceBaseRequest implements DataSourceConsoleRequestInfo {

    /**
     * sql statement
     */
    @NotNull
    private String sql;

    /**
     * console id
     */
    @NotNull
    private Long consoleId;

    /**
     *Page coding
     * Only available for select statements
     */
    private Integer pageNo;

    /**
     * Paging Size
     * Only available for select statements
     */
    private Integer pageSize;

    /**
     * Return all data
     * Only available for select statements
     */
    private Boolean pageSizeAll;

}
