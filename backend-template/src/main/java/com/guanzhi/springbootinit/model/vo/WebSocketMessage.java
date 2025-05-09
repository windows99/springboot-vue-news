package com.guanzhi.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * WebSocket消息对象
 */
@Data
public class WebSocketMessage<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 消息类型
     * 1: 系统消息
     * 2: 新闻推送
     * 3: 批量新闻推送
     */
    private Integer type;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息数据
     */
    private T data;
    
    /**
     * 批量消息数据
     */
    private List<T> dataList;
    
    /**
     * 构建单条消息
     */
    public static <T> WebSocketMessage<T> build(Integer type, String content, T data) {
        WebSocketMessage<T> message = new WebSocketMessage<>();
        message.setType(type);
        message.setContent(content);
        message.setData(data);
        return message;
    }
    
    /**
     * 构建批量消息
     */
    public static <T> WebSocketMessage<T> buildBatch(Integer type, String content, List<T> dataList) {
        WebSocketMessage<T> message = new WebSocketMessage<>();
        message.setType(type);
        message.setContent(content);
        message.setDataList(dataList);
        return message;
    }
} 