package io.metersphere.sql.service;

import io.metersphere.api.domain.ApiDefinition;
import io.metersphere.api.domain.ApiReportRelateTask;
import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.sdk.constants.ApplicationNumScope;
import io.metersphere.sdk.constants.ExecStatus;
import io.metersphere.sdk.util.BeanUtils;
import io.metersphere.sql.domain.SqlDefinition;
import io.metersphere.sql.domain.SqlExecTaskItem;
import io.metersphere.sql.domain.SqlReportRelateTask;
import io.metersphere.sql.pojo.dto.SqlDefinitionExecuteInfo;
import io.metersphere.sql.pojo.request.MsSqlCaseElement;
import io.metersphere.system.uid.IDGenerator;
import io.metersphere.system.uid.NumGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.metersphere.sql.domain.SqlExecTask;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class SqlCommonService {

    /**
     * 设置使用脚本前后置的公共脚本信息
     *
     * @param msTestElement
     */
    public void setEnableCommonScriptProcessorInfo(AbstractMsTestElement msTestElement) {
        // TODO：这里是为了以后留个口子，比如这个方法适用于前后置脚本操作
//        MsCommonElement msCommonElement = getMsCommonElement(msTestElement);
//        Optional.ofNullable(msCommonElement).ifPresent(item -> setCommonElementEnableCommonScriptInfo(List.of(item)));
    }


    public SqlExecTask newExecTask(String projectId, String userId) {
        SqlExecTask execTask = new SqlExecTask();
        execTask.setNum(NumGenerator.nextNum(ApplicationNumScope.TASK));
        execTask.setProjectId(projectId);
        execTask.setId(IDGenerator.nextStr());
        execTask.setCreateTime(System.currentTimeMillis());
        execTask.setCreateUser(userId);
        execTask.setStatus(ExecStatus.PENDING.name());
        return execTask;
    }

    public SqlExecTaskItem newExecTaskItem(String taskId, String projectId, String userId) {
        SqlExecTaskItem execTaskItem = new SqlExecTaskItem();
        execTaskItem.setCreateTime(System.currentTimeMillis());
        execTaskItem.setId(IDGenerator.nextStr());
        execTaskItem.setTaskId(taskId);
        execTaskItem.setProjectId(projectId);
        execTaskItem.setExecutor(userId);
        execTaskItem.setStatus(ExecStatus.PENDING.name());
        execTaskItem.setResourcePoolId(StringUtils.EMPTY);
        execTaskItem.setResourcePoolNode(StringUtils.EMPTY);
        return execTaskItem;
    }

    /**
     * 获取资源 ID 和接口定义信息 的 Map
     *
     * @param getDefinitionInfoFunc
     * @param resourceIds
     * @return
     */
    public Map<String, SqlDefinitionExecuteInfo> getSqlDefinitionExecuteInfoMap(Function<List<String>, List<SqlDefinitionExecuteInfo>> getDefinitionInfoFunc, List<String> resourceIds) {
        return getDefinitionInfoFunc.apply(resourceIds)
                .stream()
                .collect(Collectors.toMap(SqlDefinitionExecuteInfo::getResourceId, Function.identity()));
    }


    /**
     * 给 httpElement 设置接口定义参数
     *
     * @param apiDefinition
     * @param msTestElement
     */
    public void setSqlDefinitionExecuteInfo(AbstractMsTestElement msTestElement, SqlDefinition apiDefinition) {
        setSqlDefinitionExecuteInfo(msTestElement, BeanUtils.copyBean(new SqlDefinitionExecuteInfo(), apiDefinition));
    }

    /**
     * 设置 MsHTTPElement 中的 method 等信息
     *
     * @param msTestElement
     * @param definitionExecuteInfo
     */
    public void setSqlDefinitionExecuteInfo(AbstractMsTestElement msTestElement, SqlDefinitionExecuteInfo definitionExecuteInfo) {
        if (msTestElement instanceof MsSqlCaseElement httpElement && definitionExecuteInfo != null) {
            httpElement.setModuleId(definitionExecuteInfo.getModuleId());
        }
    }

    public SqlReportRelateTask getSqlReportRelateTask(String taskItemId, String reportId) {
        SqlReportRelateTask sqlReportRelateTask = new SqlReportRelateTask();
        sqlReportRelateTask.setReportId(reportId);
        sqlReportRelateTask.setTaskResourceId(taskItemId);
        return sqlReportRelateTask;
    }

}
