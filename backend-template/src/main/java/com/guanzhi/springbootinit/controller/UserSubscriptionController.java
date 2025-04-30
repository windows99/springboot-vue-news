package com.guanzhi.springbootinit.controller;

import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.service.UserService;
import com.guanzhi.springbootinit.service.UserSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户订阅接口
 *
 * @author sk
 */
@RestController
@RequestMapping("/user/subscription")
@Slf4j
public class UserSubscriptionController {

    @Resource
    private UserSubscriptionService userSubscriptionService;

    @Resource
    private UserService userService;

    /**
     * 保存用户的订阅标签
     *
     * @param tagIds 标签ID列表
     * @param request
     * @return
     */
    @PostMapping("/save")
    public BaseResponse<Boolean> saveSubscriptions(@RequestBody List<Long> tagIds, HttpServletRequest request) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签ID列表不能为空");
        }

        User loginUser = userService.getLoginUser(request);
        boolean result = userSubscriptionService.saveSubscriptions(loginUser.getId(), tagIds);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户的所有订阅标签
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<Map<String, Object>>> getUserSubscriptions(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<Map<String, Object>> subscriptions = userSubscriptionService.getUserSubscriptions(loginUser.getId());
        return ResultUtils.success(subscriptions);
    }

    /**
     * 检查用户是否订阅了某个标签
     *
     * @param tagId 标签ID
     * @param request
     * @return
     */
    @GetMapping("/check")
    public BaseResponse<Boolean> isSubscribed(@RequestParam("tagId") Long tagId, HttpServletRequest request) {
        if (tagId == null || tagId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签ID不合法");
        }

        User loginUser = userService.getLoginUser(request);
        boolean result = userSubscriptionService.isSubscribed(loginUser.getId(), tagId.toString());
        return ResultUtils.success(result);
    }
} 