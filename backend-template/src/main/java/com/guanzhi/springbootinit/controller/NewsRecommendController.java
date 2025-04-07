package com.guanzhi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.annotation.AuthCheck;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.constant.UserConstant;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.service.NewsRecommendService;
import com.guanzhi.springbootinit.service.NewsPushService;
import com.guanzhi.springbootinit.service.NewsWebSocketServer;
import com.guanzhi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/news/recommend")
@Slf4j
public class NewsRecommendController {

    @Resource
    private NewsRecommendService newsRecommendService;
    
    @Resource
    private NewsPushService newsPushService;
    
    @Resource
    private UserService userService;

    /**
     * 获取个性化推荐新闻（分页）
     */
    @GetMapping("/forUser/{userId}")
    public BaseResponse<Page<NewsRecommendDTO>> getRecommendForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        Page<NewsRecommendDTO> page = newsRecommendService.getPersonalizedNewsPage(userId, current, pageSize);
        return ResultUtils.success(page);
    }
    
    /**
     * 获取热门新闻（分页）
     */
    @GetMapping("/hot")
    public BaseResponse<Page<NewsRecommendDTO>> getHotNews(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        Page<NewsRecommendDTO> page = newsRecommendService.getHotNewsPage(current, pageSize);
        return ResultUtils.success(page);
    }
    
    /**
     * 手动触发推送（仅管理员可用）
     */
    @PostMapping("/push")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> pushNews(@RequestParam(required = false) Long userId) {
        boolean result;
        if (userId != null) {
            // 推送给指定用户
            result = newsPushService.pushNewsToUser(userId);
            
            // 通过WebSocket发送通知
//            try {
//                NewsWebSocketServer.sendInfo("您有新的推荐新闻，请查看", userId.toString());
//            } catch (Exception e) {
//                log.error("WebSocket推送失败: {}", e.getMessage());
//            }
        } else {
            // 推送给所有用户
            int count = newsPushService.pushNewsToAllUsers();
            result = count > 0;
        }
        return ResultUtils.success(result);
    }
    
    /**
     * 测试WebSocket推送
     */
    @GetMapping("/test-ws/{userId}")
    public BaseResponse<Boolean> testWebSocket(@PathVariable String userId, @RequestParam String message) {
        try {
            NewsWebSocketServer.sendInfo(message, userId);
            return ResultUtils.success(true);
        } catch (Exception e) {
            log.error("WebSocket测试失败: {}", e.getMessage());
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "WebSocket推送失败");
        }
    }
} 