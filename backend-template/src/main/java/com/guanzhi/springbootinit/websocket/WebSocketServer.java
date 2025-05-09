package com.guanzhi.springbootinit.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务器
 */
@ServerEndpoint("/websocket/{userId}")
@Component
@Slf4j
public class WebSocketServer {

    /**
     * 用户连接池
     */
    private static final Map<String, Session> USER_SESSIONS = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        log.info("用户 {} 建立WebSocket连接", userId);
        USER_SESSIONS.put(userId, session);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        log.info("用户 {} 关闭WebSocket连接", userId);
        USER_SESSIONS.remove(userId);
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        log.info("收到用户 {} 的消息: {}", userId, message);
        // 这里可以处理客户端发送的消息
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误: {}", error.getMessage(), error);
    }

    /**
     * 发送消息给指定用户
     */
    public static void sendMessageToUser(String userId, String message) {
        Session session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("向用户 {} 发送消息成功", userId);
            } catch (IOException e) {
                log.error("向用户 {} 发送消息失败: {}", userId, e.getMessage(), e);
            }
        } else {
            log.warn("用户 {} 不在线，无法发送消息", userId);
        }
    }

    /**
     * 判断用户是否在线
     */
    public static boolean isUserOnline(String userId) {
        Session session = USER_SESSIONS.get(userId);
        return session != null && session.isOpen();
    }
} 