package com.guanzhi.springbootinit.websocket;

import com.alibaba.fastjson.JSON;
import com.guanzhi.springbootinit.model.vo.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocket服务器
 *
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class WebSocketServer {

    // 记录当前在线连接数
    private static AtomicInteger onlineCount = new AtomicInteger(0);
    
    // 存放每个客户端对应的WebSocketServer对象
    private static Map<String, WebSocketServer> clients = new ConcurrentHashMap<>();
    
    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    // 当前连接用户ID
    private String userId = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        
        // 如果已存在相同用户的WebSocket连接，先关闭它
        if (clients.containsKey(userId)) {
            try {
                clients.get(userId).session.close();
            } catch (IOException e) {
                log.error("关闭已存在的连接失败: {}", e.getMessage());
            }
            clients.remove(userId);
            onlineCount.decrementAndGet();
        }
        
        // 添加新连接
        clients.put(userId, this);
        onlineCount.incrementAndGet();
        
        log.info("用户{}连接成功，当前在线人数为：{}", userId, onlineCount.get());
        
        try {
            // 发送连接成功消息
            WebSocketMessage message = WebSocketMessage.build(1, "连接成功");
            sendMessage(JSON.toJSONString(message));
        } catch (IOException e) {
            log.error("用户{}连接异常：{}", userId, e.getMessage());
        }
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (clients.containsKey(userId)) {
            clients.remove(userId);
            onlineCount.decrementAndGet();
            log.info("用户{}断开连接，当前在线人数为：{}", userId, onlineCount.get());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到用户{}的消息：{}", userId, message);
        
        // 可以在这里处理接收到的消息，例如转发给其他用户或进行其他业务处理
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户{}连接发生错误：{}", userId, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 发送消息给指定用户
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    public static void sendMessageToUser(String userId, String message) {
        if (clients.containsKey(userId)) {
            try {
                clients.get(userId).sendMessage(message);
                log.info("发送消息给用户{}：{}", userId, message);
            } catch (IOException e) {
                log.error("发送消息给用户{}失败：{}", userId, e.getMessage());
            }
        } else {
            log.warn("用户{}不在线", userId);
        }
    }

    /**
     * 发送广播消息给所有用户
     *
     * @param message 消息内容
     */
    public static void sendMessageToAll(String message) {
        clients.forEach((userId, client) -> {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                log.error("广播消息给用户{}失败：{}", userId, e.getMessage());
            }
        });
        log.info("广播消息给所有用户：{}", message);
    }

    /**
     * 发送消息到当前WebSocket客户端
     *
     * @param message 消息内容
     */
    private void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 获取当前在线人数
     *
     * @return 在线人数
     */
    public static int getOnlineCount() {
        return onlineCount.get();
    }
    
    /**
     * 判断用户是否在线
     * 
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isUserOnline(String userId) {
        return clients.containsKey(userId);
    }
} 