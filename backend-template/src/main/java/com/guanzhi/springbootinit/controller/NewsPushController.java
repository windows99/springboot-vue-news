package com.guanzhi.springbootinit.controller;

import com.guanzhi.springbootinit.annotation.AuthCheck;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.constant.UserConstant;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.news.NewsPushConfigDTO;
import com.guanzhi.springbootinit.model.dto.news.NewsPushDTO;
import com.guanzhi.springbootinit.service.NewsPushConfigService;
import com.guanzhi.springbootinit.service.NewsPushService;
import com.guanzhi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 新闻推送控制器
 */
@RestController
@RequestMapping("/news/push")
@Slf4j
public class NewsPushController {

    @Resource
    private NewsPushService newsPushService;
    
    @Resource
    private NewsPushConfigService newsPushConfigService;
    
    @Resource
    private UserService userService;

    /**
     * 手动推送个性化新闻给指定用户
     */
    @PostMapping("/user/{userId}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> pushNewsToUser(@PathVariable Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        boolean result = newsPushService.pushNewsToUser(userId);
        return ResultUtils.success(result);
    }
    
    /**
     * 手动推送个性化新闻给所有用户
     */
    @PostMapping("/all")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> pushNewsToAllUsers() {
        int count = newsPushService.pushNewsToAllUsers();
        return ResultUtils.success(count);
    }
    
    /**
     * 推送指定新闻给指定用户
     */
    @PostMapping("/specific")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> pushSpecificNews(
            @RequestParam Long userId,
            @RequestParam Long newsId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (newsId == null || newsId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻ID不合法");
        }
        
        boolean result = newsPushService.pushSpecificNewsToUser(userId, newsId);
        return ResultUtils.success(result);
    }
    
    /**
     * 推送指定新闻给所有用户
     */
    @PostMapping("/specific/all")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> pushSpecificNewsToAll(@RequestParam Long newsId) {
        if (newsId == null || newsId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻ID不合法");
        }
        
        int count = newsPushService.pushSpecificNewsToAllUsers(newsId);
        return ResultUtils.success(count);
    }
    
    /**
     * 批量推送多个指定新闻给指定用户
     */
    @PostMapping("/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> pushMultipleNewsToUser(
            @RequestParam Long userId,
            @RequestBody List<Long> newsIds) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (newsIds == null || newsIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻ID列表不能为空");
        }
        
        int count = newsPushService.pushMultipleNewsToUser(userId, newsIds);
        return ResultUtils.success(count);
    }
    
    /**
     * 批量推送多个指定新闻给所有用户
     */
    @PostMapping("/batch/all")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> pushMultipleNewsToAllUsers(@RequestBody List<Long> newsIds) {
        if (newsIds == null || newsIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻ID列表不能为空");
        }
        
        int count = newsPushService.pushMultipleNewsToAllUsers(newsIds);
        return ResultUtils.success(count);
    }
    
    /**
     * 设置定时推送个性化新闻给用户
     */
    @PostMapping("/schedule/user/{userId}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> schedulePushToUser(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date pushTime) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        if (pushTime == null || pushTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "推送时间不合法");
        }
        
        boolean result = newsPushService.schedulePushToUser(userId, pushTime);
        return ResultUtils.success(result);
    }
    
    /**
     * 设置定时推送个性化新闻给所有用户
     */
    @PostMapping("/schedule/all")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> schedulePushToAllUsers(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date pushTime) {
        if (pushTime == null || pushTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "推送时间不合法");
        }
        
        int count = newsPushService.schedulePushToAllUsers(pushTime);
        return ResultUtils.success(count);
    }
    
    /**
     * 获取推送配置列表
     */
    @GetMapping("/config/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<NewsPushConfigDTO>> getConfigList() {
        List<NewsPushConfigDTO> configList = newsPushConfigService.getConfigList();
        return ResultUtils.success(configList);
    }
    
    /**
     * 添加或更新推送配置
     */
    @PostMapping("/config/save")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> saveConfig(@RequestBody NewsPushConfigDTO config) {
        if (config == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        
        boolean result = newsPushConfigService.saveConfig(config);
        return ResultUtils.success(result);
    }
    
    /**
     * 删除推送配置
     */
    @DeleteMapping("/config/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteConfig(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "配置ID不合法");
        }
        
        boolean result = newsPushConfigService.deleteConfig(id);
        return ResultUtils.success(result);
    }
    
    /**
     * 启用或禁用推送配置
     */
    @PostMapping("/config/status/{id}")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateConfigStatus(
            @PathVariable Long id,
            @RequestParam Boolean enabled) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "配置ID不合法");
        }
        
        boolean result = newsPushConfigService.updateConfigStatus(id, enabled);
        return ResultUtils.success(result);
    }
    
    /**
     * 获取用户的推送历史
     */
    @GetMapping("/history/{userId}")
    public BaseResponse<List<NewsPushDTO>> getUserPushHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        List<NewsPushDTO> history = newsPushService.getUserPushHistory(userId, limit);
        return ResultUtils.success(history);
    }
    
    /**
     * 标记推送为已读
     */
    @PostMapping("/read/{pushId}")
    public BaseResponse<Boolean> markPushAsRead(@PathVariable Long pushId) {
        if (pushId == null || pushId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "推送ID不合法");
        }
        
        boolean result = newsPushService.markPushAsRead(pushId);
        return ResultUtils.success(result);
    }
} 