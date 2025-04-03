package io.metersphere.sql.service.scenario;

import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.sdk.constants.ApiExecuteRunMode;
import io.metersphere.sdk.dto.api.task.TaskInfo;
import io.metersphere.sdk.dto.api.task.TaskItem;
import io.metersphere.sdk.dto.api.task.TaskRequestDTO;
import io.metersphere.sdk.exception.MSException;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.context.Chat2DBContext;
import io.metersphere.sql.pojo.dto.SqlParamConfig;
import io.metersphere.sql.pojo.dto.debug.SqlResourceRunRequest;
import io.metersphere.sql.pojo.model.Command;
import io.metersphere.sql.pojo.request.MsSqlCaseElement;
import io.metersphere.sql.pojo.request.SqlMsScenario;
import io.metersphere.sql.spi.service.CommandExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SqlExecuteService {

    public TaskInfo getTaskInfo(String projectId) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setProjectId(projectId);
        return taskInfo;
    }

    public TaskItem getTaskItem(String reportId, String resourceId) {
        TaskItem taskItem = new TaskItem();
        taskItem.setReportId(reportId);
        taskItem.setResourceId(resourceId);
        taskItem.setRequestCount(1L);
        return taskItem;
    }

    public TaskRequestDTO execute(SqlResourceRunRequest runRequest, TaskRequestDTO taskRequest, SqlParamConfig parameterConfig) {
        TaskInfo taskInfo = taskRequest.getTaskInfo();
        TaskItem taskItem = taskRequest.getTaskItem();

        // 这里直接将待执行的场景内容转为json，发送到kafka消息中去
        // 不清楚原ms为何会将待执行内容转为jmeter结构后放入redis，难道是担心kafka消息可能太长导入写入失败？
        // 对于sql测试，由于存在较长的结果校验内容，因此需要考虑消息内容长度过长的特殊场景

        // 如果是SqlCase级别，那么直接将用例写入topic
        // 如果是SqlScenario，则需要合并config部分后，将SqlScenario中的children子步骤顺序写入
        // TODO：1.这里目前对children部分仅做简单写入，并没有做配置合并，以ap接口测试为例，实际最上层会有一些全局配置或前后置操作，
        //  这些应该都在每个步骤或特殊步骤中有体现，但是目前SQL测试比较简单，并不支持前后置和配置，所以只需要考虑children部分写入即可
        // TODO：2.目前只支持mysql协议，所以这里写死sql_plugin的dbType为mysql，如果以后支持其他协议，那么这里直接从前端传过来的参数中获取即可
        CommandExecutor executor = Chat2DBContext.getMetaData("MYSQL").getCommandExecutor();

        log.info(JSON.toJSONString(runRequest));
        for(SqlAbstractMsTestElement each : runRequest.getTestElement().getSqlChildren()){
            if (each instanceof MsSqlCaseElement) {
                each.setPolymorphicName(MsSqlCaseElement.class.getName());
                each.setReportId(taskItem.getReportId());
                executor.sendSqlScenarioRunTaskMessage(each);
            }else if(each instanceof SqlMsScenario){
                // TODO：如果是场景嵌套的话，暂时跳过，后续补充
            }else{
                throw new MSException("MsTestElement数据异常");
            }
        }

        return taskRequest;


        // 将request中的所有步骤按顺序写入kafka
//        String executeScript = parseExecuteScript(runRequest.getTestElement(), parameterConfig);
        // 脚本已经解析，不需要再解析
//        taskInfo.setNeedParseScript(false);

        // 将测试脚本缓存到 redis
//        String scriptRedisKey = taskItem.getId();
//        stringRedisTemplate.opsForValue().set(scriptRedisKey, executeScript, 1, TimeUnit.DAYS);
//
//        setTaskItemFileParam(runRequest, taskItem);
//
//        if (ApiExecuteRunMode.isFrontendDebug(taskInfo.getRunMode())) {
//            taskInfo = setTaskRequestParams(taskInfo);
//            // 清空mino和kafka配置信息，避免前端获取
//            taskInfo.setMinioConfig(null);
//            taskInfo.setKafkaConfig(null);
//            // 前端调试返回执行参数，由前端调用本地资源池执行
//            return taskRequest;
//        }
//
//        try {
//            return execute(taskRequest);
//        } catch (Exception e) {
//            // 调用失败清理脚本
//            stringRedisTemplate.delete(scriptRedisKey);
//            throw e;
//        }
//        return null;
    }
}
