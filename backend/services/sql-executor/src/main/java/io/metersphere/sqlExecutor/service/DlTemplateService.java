package io.metersphere.sqlExecutor.service;

import io.metersphere.sqlExecutor.pojo.model.ExecuteResult;
import io.metersphere.sqlExecutor.pojo.params.DlExecuteParam;
import io.metersphere.sqlExecutor.wrapper.result.ListResult;

/**
 * Data source management services
 *
 * @author moji
 * @version DataSourceCoreService.java, v 0.1 September 23, 2022 15:22 moji Exp $
 * @date 2022/09/23
 */
public interface DlTemplateService {

    /**
     * data source execution dl
     *
     * @param param
     * @return
     */
    ListResult<ExecuteResult> execute(DlExecuteParam param);


}
