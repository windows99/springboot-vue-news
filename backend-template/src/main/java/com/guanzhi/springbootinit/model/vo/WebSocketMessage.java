package com.guanzhi.springbootinit.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * WebSocket消息实体
 */
@Data
@Accessors(chain = true)
public class WebSocketMessage {
    /**
     * 消息类型
     * 1: 系统消息
     * 2: 新闻推送
     * 3: 用户消息
     */
    private Integer type;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 发送时间
     */
    private Long timestamp;
    
    /**
     * 额外数据（可选）
     */
    private Object data;
    
    public static WebSocketMessage build(Integer type, String content) {
        return new WebSocketMessage()
            .setType(type)
            .setContent(content)
            .setTimestamp(System.currentTimeMillis());
    }
    
    public static WebSocketMessage build(Integer type, String content, Object data) {
        return build(type, content).setData(data);
    }
} 