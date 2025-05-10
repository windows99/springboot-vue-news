package com.guanzhi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.NewsPushDTO;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.vo.NewsPushVO;
import com.guanzhi.springbootinit.service.NewsPushService;
import com.guanzhi.springbootinit.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 新闻推送接口
 */
@RestController
@RequestMapping("/api/news/push")
public class NewsPushController {

    @Autowired
    private NewsPushService newsPushService;
    
    @Autowired
    private UserService userService;

    /**
     * 立即推送新闻
     *
     * @param newsId 新闻ID
     * @param userIds 用户ID列表（可选，不传则推送给所有用户）
     * @param request HTTP请求
     * @return 是否推送成功
     */
    @PostMapping("/immediately")
    public BaseResponse<Boolean> pushImmediately(@RequestParam Long newsId, 
                                               @RequestParam(required = false) List<Long> userIds,
                                               HttpServletRequest request) {
        // 验证用户是否有权限推送给所有用户
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可执行此操作");
        }
        
        if (newsId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻ID不能为空");
        }
        
        // 如果没有指定用户，则推送给所有用户
        if (userIds == null || userIds.isEmpty()) {
            userIds = userService.getAllUserIds();
        }
        
        List<Long> newsIds = Collections.singletonList(newsId);
        boolean result = newsPushService.pushImmediately(newsIds, userIds);
        return ResultUtils.success(result);
    }

    /**
     * 批量推送新闻
     * 
     * 前端需要传递的参数：
     * {
     *     "newsIds": [1, 2, 3],     // 必填，要推送的新闻ID列表
     *     "userIds": [1, 2, 3]      // 可选，接收推送的用户ID列表，不传则推送给所有用户
     * }
     */
    @PostMapping("/batch")
    public BaseResponse<Integer> pushBatch(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        
        // 验证用户是否有权限推送给所有用户
        if (!userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "仅管理员可执行此操作");
        }
        
        // 获取参数
        if (!params.containsKey("newsIds") || params.get("newsIds") == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻ID列表不能为空");
        }
        
        List<Long> newsIds = new ArrayList<>();
        List<?> newsIdList = (List<?>) params.get("newsIds");
        for (Object newsId : newsIdList) {
            newsIds.add(Long.parseLong(newsId.toString()));
        }
        
        List<Long> userIds;
        // 如果没有指定用户，则推送给所有用户
        if (!params.containsKey("userIds") || params.get("userIds") == null) {
            userIds = userService.getAllUserIds();
        } else {
            // 从参数中获取用户ID列表
            userIds = new ArrayList<>();
            List<?> userIdList = (List<?>) params.get("userIds");
            for (Object userId : userIdList) {
                userIds.add(Long.parseLong(userId.toString()));
            }
        }
        
        // 执行批量推送
        int result = newsPushService.pushMultipleNews(newsIds, userIds);
        
        return ResultUtils.success(result);
    }

    /**
     * 个性化推送
     */
    @PostMapping("/personalized")
    public BaseResponse<Integer> pushPersonalized(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        // 获取参数
        Integer limit = 5; // 默认推送5条
        if (params.containsKey("limit") && params.get("limit") != null) {
            limit = Integer.parseInt(params.get("limit").toString());
        }
        
        // 执行个性化推送
        int result = newsPushService.pushPersonalizedNews(userId, limit);
        
        return ResultUtils.success(result);
    }

    /**
     * 获取未读推送
     */
    @GetMapping("/unread")
    public BaseResponse<List<NewsPushDTO>> getUnreadPushes(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        // 获取未读推送
        List<NewsPushDTO> pushes = newsPushService.getUnreadPushes(userId);
        
        return ResultUtils.success(pushes);
    }

    /**
     * 标记为已读
     */
    @PostMapping("/read")
    public BaseResponse<Boolean> markAsRead(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        userService.getLoginUser(request);
        
        // 获取参数
        if (!params.containsKey("pushId")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "推送ID不能为空");
        }
        
        Long pushId = Long.parseLong(params.get("pushId").toString());
        
        // 标记为已读
        boolean result = newsPushService.markAsRead(pushId);
        
        return ResultUtils.success(result);
    }

    /**
     * 获取推送记录列表
     */
    @GetMapping("/record/list")
    public BaseResponse<Page<NewsPushVO>> listPushRecords(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Integer isRead,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        // 获取推送记录列表（包含新闻标题）
        Page<NewsPushVO> page = newsPushService.getUserPushRecordsWithNews(userId, current, pageSize, isRead);
        
        return ResultUtils.success(page);
    }

    /**
     * 获取推送记录详情
     */
    @GetMapping("/record/{recordId}")
    public BaseResponse<NewsPush> getPushRecordDetail(
            @PathVariable Long recordId,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        
        // 获取推送记录详情
        NewsPush record = newsPushService.getPushRecordDetail(recordId, userId);
        
        return ResultUtils.success(record);
    }
} 