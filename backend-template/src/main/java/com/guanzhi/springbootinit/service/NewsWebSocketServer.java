package com.guanzhi.springbootinit.service;

import com.alibaba.fastjson.JSON;
import com.guanzhi.springbootinit.model.vo.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/websocket/news/{userId}")
@Component
@Slf4j
public class NewsWebSocketServer {

    // 用来存放每个客户端对应的WebSocket对象
    private static final ConcurrentHashMap<String, NewsWebSocketServer> webSocketMap = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收userId
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;

        // 如果已存在相同userId的WebSocket，先移除
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }

        webSocketMap.put(userId, this);
        log.info("用户连接:{}，当前在线人数:{}", userId, webSocketMap.size());

        try {
            WebSocketMessage message = WebSocketMessage.build(1, "连接成功");
            sendMessage(JSON.toJSONString(message));
        } catch (IOException e) {
            log.error("用户:{}，网络异常", userId);
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
        }
        log.info("用户退出:{}，当前在线人数:{}", userId, webSocketMap.size());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:{}，报文:{}", userId, message);
        // 可以在这里处理客户端发送的消息
        try {
            WebSocketMessage response = WebSocketMessage.build(3, "消息已收到：" + message);
            sendMessage(JSON.toJSONString(response));
        } catch (IOException e) {
            log.error("回复用户消息失败", e);
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:{}，原因:{}", userId, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 发送消息
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息到指定用户
     */
    public static void sendInfo(String message, String userId) {
        WebSocketMessage wsMessage = WebSocketMessage.build(2, message);
        sendInfo(wsMessage, userId);
    }

    /**
     * 发送WebSocketMessage到指定用户
     */
    public static void sendInfo(WebSocketMessage message, String userId) {
        log.info("发送消息到:{}，消息:{}", userId, JSON.toJSONString(message));
        if (webSocketMap.containsKey(userId)) {
            try {
                webSocketMap.get(userId).sendMessage(JSON.toJSONString(message));
            } catch (IOException e) {
                log.error("发送消息异常:{}", e.getMessage());
            }
        } else {
            log.error("用户{}不在线", userId);
        }
    }

    /**
     * 群发消息
     */
    public static void broadcastMessage(String message) {
        WebSocketMessage wsMessage = WebSocketMessage.build(1, message);
        broadcastMessage(wsMessage);
    }

    /**
     * 群发WebSocketMessage
     */
    public static void broadcastMessage(WebSocketMessage message) {
        log.info("群发消息，当前在线人数:{}，消息:{}", webSocketMap.size(), JSON.toJSONString(message));
        webSocketMap.forEach((userId, ws) -> {
            try {
                ws.sendMessage(JSON.toJSONString(message));
            } catch (IOException e) {
                log.error("群发消息异常，用户:{}，异常:{}", userId, e.getMessage());
            }
        });
    }

    /**
     * 获取当前在线用户数
     */
    public static int getOnlineCount() {
        return webSocketMap.size();
    }

    /**
     * 判断用户是否在线
     */
    public static boolean isOnline(String userId) {
        return webSocketMap.containsKey(userId);
    }
}