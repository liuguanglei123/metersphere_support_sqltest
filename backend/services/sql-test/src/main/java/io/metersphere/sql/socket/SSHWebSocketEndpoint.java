package io.metersphere.sql.socket;

import com.alibaba.fastjson2.JSONObject;
import com.jcraft.jsch.SftpException;
import io.metersphere.sdk.constants.MsgType;
import io.metersphere.sdk.dto.SocketMsgDTO;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sdk.util.LogUtils;
import io.metersphere.sdk.util.WebSocketUtils;
import io.metersphere.sql.pojo.model.SSHMessage;
import io.metersphere.sql.utils.JschUtil;
import jakarta.websocket.*;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/terminal/ssh/{id}")
public class SSHWebSocketEndpoint {

    //在线客户端集合
    public static final Map<String, Session> onlineSessionClientMap = new ConcurrentHashMap<>();

    /**
     * 连接创建成功
     *
     * @param id
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("id") String id, Session session) {
        log.info("开启连接{}", id);
        onlineSessionClientMap.put(id, session);
        RemoteEndpoint.Async async = session.getAsyncRemote();
        if (async != null) {
//            async.sendText(JSON.toJSONString(new SocketMsgDTO(id, "", MsgType.SQL_CONNECT.name(), MsgType.SQL_CONNECT.name())));
            session.setMaxIdleTimeout(180000);
        }

        try {
            // 一开始建立的时候连接服务器
            JschUtil.getConnectedSession(id);
        } catch (JSchException | SftpException e) {
            log.error(e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 连接关闭回调
     *
     * @param id
     * @param session
     */
    @OnClose
    public void onClose(@PathParam("id") String id, Session session) {
        //从map集合中移除
        log.info("断开连接{}", id);
        onlineSessionClientMap.remove(id);
        JschUtil.close(id);
    }

    /**
     * 收到消息后的回调
     *
     * @param message
     */
    @OnMessage
    public void onMessage(@PathParam("id") String connectId, String message) {
        LogUtils.info("服务器收到：[" + connectId + "] : " + message);

        if(message.equals("__ping__"))
            return;
        SSHMessage msg = JSONObject.parseObject(message, SSHMessage.class);
        if (msg != null && msg.getTo() != null) {
            JschUtil.execCommand(connectId,msg.getContent());
        }
    }

    /**
     * 发生错误时的回调
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
    }
//    private com.jcraft.jsch.Session sshSession;
//    private ChannelShell channel;
//    private OutputStream outputStream;
//
//    @OnOpen
//    public void onOpen(Session session) throws Exception {
//        System.out.println("WebSocket opened: " + session.getId());
//        connectToSSH();
//        PipedInputStream pipeIn = new PipedInputStream();
//        PipedOutputStream pipeOut = new PipedOutputStream(pipeIn);
//        channel.setOutputStream(pipeOut);
//
//        // 将 SSH 输出发送到 WebSocket
//        new Thread(() -> {
//            try (BufferedReader reader = new BufferedReader(new InputStreamReader(pipeIn))) {
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    session.getBasicRemote().sendText(line + "\n");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        outputStream = channel.getOutputStream();
//    }
//
//    @OnMessage
//    public void onMessage(String message, Session session) throws Exception {
//        if (outputStream != null) {
//            outputStream.write((message + "\n").getBytes());
//            outputStream.flush();
//        }
//    }
//
//    @OnClose
//    public void onClose(Session session) {
//        System.out.println("WebSocket closed: " + session.getId());
//        if (channel != null) {
//            channel.disconnect();
//        }
//        if (sshSession != null) {
//            sshSession.disconnect();
//        }
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) {
//        System.out.println("WebSocket error: " + throwable.getMessage());
//    }
//
//
//    private void connectToSSH() throws JSchException {
//        JSch jsch = new JSch();
//        String host = "172.30.14.238";
//        String user = "root";
//        String password = "ZhangJiu#240415";
//        int port = 22;
//
//        sshSession = jsch.getSession(user, host, port);
//        sshSession.setPassword(password);
//        sshSession.setConfig("StrictHostKeyChecking", "no");
//        sshSession.connect();
//
//        channel = (ChannelShell) sshSession.openChannel("shell");
//        channel.connect();
//    }
}
