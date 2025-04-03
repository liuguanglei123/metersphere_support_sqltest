package io.metersphere.sql.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SSHMessage {
    // 发送方
    private String from;

    // 接收方
    private String to;

    // 消息内容
    private String content;
}
