package com.guanzhi.springbootinit.controller;

import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.websocket.NewsWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/websocket")
@Slf4j
public class WebSocketTestController {

    /**
     * 测试WebSocket连接
     */
    @GetMapping("/test/{userId}")
    public BaseResponse<String> testWebSocket(@PathVariable String userId) {
        try {
            NewsWebSocketServer.sendInfo("这是一条测试消息，来自服务器", userId);
            return ResultUtils.success("消息发送成功");
        } catch (Exception e) {
            log.error("WebSocket测试失败: {}", e.getMessage());
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "WebSocket测试失败");
        }
    }
} 