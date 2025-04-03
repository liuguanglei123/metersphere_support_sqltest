package io.metersphere.sql.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import io.metersphere.sdk.constants.KafkaTopicConstants;
import io.metersphere.sdk.constants.MsgType;
import io.metersphere.sdk.dto.SocketMsgDTO;
import io.metersphere.sdk.dto.result.ListResult;
import io.metersphere.sdk.util.CommonBeanFactory;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sdk.util.LogUtils;
import io.metersphere.sdk.util.WebSocketUtils;
import io.metersphere.sql.pojo.model.DiffExecuteResult;
import io.metersphere.sql.service.scenario.SqlScenarioReportService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 后端SQL调试消息监听器，当监听到SQL_REPORT_DEBUG_TASK_RESULT_TOPIC消息时说明SQL已经执行完成，则将消息推送给前端
 * 为什么这里选择通过kafka消息接收结果通知而不是直接在获取到结果后发送给前端，是为了未来做服务分离做准备
 */
@Component
public class SQLDebugListener {
    public static final String SQL_DEBUG_CONSUME_ID = "MS-SQL-DEBUG-CONSUME";
    public static final String SQL_SCENARIO_REPORT_FRONT_CONSUME_ID = "SQL_SCENARIO_REPORT_FRONT_CONSUME_ID";
    public static final String SQL_SCENARIO_REPORT_RESULT_HUB_CONSUME_ID = "SQL_SCENARIO_REPORT_RESULT_HUB_CONSUME_ID";

    @Resource
    SqlScenarioReportService sqlScenarioReportService;

    @KafkaListener(id = SQL_DEBUG_CONSUME_ID, topics = KafkaTopicConstants.SQL_REPORT_DEBUG_TASK_RESULT_TOPIC, groupId = SQL_DEBUG_CONSUME_ID + "_" + "${random.uuid}")
    public void debugConsume(ConsumerRecord<?, String> record) {
        try {
            LogUtils.info("接收到执行结果：keys is {}, values is {}", record.key(),record.value());
            if (ObjectUtils.isNotEmpty(record.value()) && WebSocketUtils.has(record.key().toString())) {
                SocketMsgDTO dto = JSON.parseObject(record.value(), SocketMsgDTO.class);

                LogUtils.info("{} 推送执行结果类型【 {} 】", record.key(), dto.getMsgType());
                WebSocketUtils.sendMessageSingle(dto);
            }
        } catch (Exception e) {
            LogUtils.error("{} 调试消息推送失败：{}", record.key(), e);
        }
    }

    /**
     * 消费场景执行结果的消息，将执行结果保存到数据库中，至于为何要和下面与前端的同步数据分开，为了以后方便扩展吧，和ms api测试保持一致，可能以后也会有一个result-hub-sql的单独服务
     * @param record
     */
    @KafkaListener(id = SQL_SCENARIO_REPORT_FRONT_CONSUME_ID, topics = KafkaTopicConstants.SQL_SCENARIO_REPORT_TOPIC, groupId = SQL_SCENARIO_REPORT_FRONT_CONSUME_ID + "_" + "${random.uuid}")
    public void debugConsume3(ConsumerRecord<?, String> record) {
        try {
            if (ObjectUtils.isNotEmpty(record.value())) {
                SocketMsgDTO dto = JSON.parseObject(record.value(), SocketMsgDTO.class);

                // 创建 TypeReference 指定泛型类型 ListResult<ExecuteResult>
//                TypeReference<ListResult<DiffExecuteResult>> typeRef
//                        = new TypeReference<ListResult<DiffExecuteResult>>() {};
//                ListResult<DiffExecuteResult> diffExecuteResult = JSON.parseListResult(dto.getTaskResult(),typeRef);

                LogUtils.info("{} 推送执行结果类型【 {} 】", record.key(), dto.getMsgType());
                WebSocketUtils.sendMessageSingle(dto);
            }
        } catch (Exception e) {
            LogUtils.error("{} 调试消息推送失败：{}", record.key(), e);
        }
    }

    /**
     * 同样是消费场景执行结果的消息，将执行结果同步到前端
     * @param record
     */
    @KafkaListener(id = SQL_SCENARIO_REPORT_RESULT_HUB_CONSUME_ID, topics = KafkaTopicConstants.SQL_SCENARIO_REPORT_TOPIC, groupId = SQL_SCENARIO_REPORT_RESULT_HUB_CONSUME_ID + "_" + "${random.uuid}")
    public void debugConsume2(ConsumerRecord<?, String> record) {
        try {
            // TODO：1.更新数据库的任务状态信息，并记录blob，更新detail
            //  2.推送消息给前端，展示执行状态，前端点击后可以查看执行结果
            if (ObjectUtils.isNotEmpty(record.value())) {
                SocketMsgDTO dto = JSON.parseObject(record.value(), SocketMsgDTO.class);

                LogUtils.info("SQL_SCENARIO_REPORT_TOPIC: {} 推送执行结果类型【 {} 】", record.key(), dto.getMsgType());
                if(dto.getMsgType().equals(MsgType.SQL_EXEC_RESULT.name())) {
                    sqlScenarioReportService.saveReportDetail(dto);
                }
            }
        } catch (Exception e) {
            LogUtils.error("{} 调试消息推送失败：{}", record.key(), e);
        }
    }
}
