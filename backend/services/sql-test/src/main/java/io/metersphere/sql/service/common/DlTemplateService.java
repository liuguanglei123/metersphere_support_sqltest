package io.metersphere.sql.service.common;

import io.metersphere.sql.pojo.model.ExecuteResult;
import io.metersphere.sql.pojo.params.DlExecuteParam;
import io.metersphere.sql.wrapper.result.ListResult;

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
    void execute(DlExecuteParam param);

    /**
     * data source execution dl direct
     *
     * @param param
     * @return
     */
    ListResult<ExecuteResult> executeDirect(DlExecuteParam param);
}
