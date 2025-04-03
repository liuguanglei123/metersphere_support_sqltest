package io.metersphere.sql.pojo.dto.debug;

import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.*;

@Data
public class SqlResourceRunRequest {
    /**
     * 执行组件
     */
    private SqlAbstractMsTestElement testElement;

    /**
     * 执行的资源所属项目的ID列表
     * 场景执行时，为引用的资源的项目ID列表
     */
    private Set<String> refProjectIds = HashSet.newHashSet(0);
}
