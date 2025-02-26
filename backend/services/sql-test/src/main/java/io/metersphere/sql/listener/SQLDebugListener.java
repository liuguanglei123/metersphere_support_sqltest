package io.metersphere.sql.listener;

import io.metersphere.sdk.constants.KafkaTopicConstants;
import io.metersphere.sdk.constants.MsgType;
import io.metersphere.sdk.dto.SocketMsgDTO;
import io.metersphere.sdk.util.CommonBeanFactory;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sdk.util.LogUtils;
import io.metersphere.sdk.util.WebSocketUtils;
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
}
