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
     * 添加订阅
     *
     * @param category 订阅分类
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addSubscription(@RequestParam("category") String category, HttpServletRequest request) {
        if (StringUtils.isBlank(category)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订阅分类不能为空");
        }

        User loginUser = userService.getLoginUser(request);
        boolean result = userSubscriptionService.addSubscription(loginUser.getId(), category);
        return ResultUtils.success(result);
    }

    /**
     * 取消订阅
     *
     * @param category 订阅分类
     * @param request
     * @return
     */
    @PostMapping("/cancel")
    public BaseResponse<Boolean> cancelSubscription(@RequestParam("category") String category, HttpServletRequest request) {
        if (StringUtils.isBlank(category)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订阅分类不能为空");
        }

        User loginUser = userService.getLoginUser(request);
        boolean result = userSubscriptionService.cancelSubscription(loginUser.getId(), category);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户的所有订阅分类
     *
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<String>> getUserSubscriptions(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<String> subscriptions = userSubscriptionService.getUserSubscriptions(loginUser.getId());
        return ResultUtils.success(subscriptions);
    }

    /**
     * 检查用户是否订阅了某个分类
     *
     * @param category 订阅分类
     * @param request
     * @return
     */
    @GetMapping("/check")
    public BaseResponse<Boolean> isSubscribed(@RequestParam("category") String category, HttpServletRequest request) {
        if (StringUtils.isBlank(category)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订阅分类不能为空");
        }

        User loginUser = userService.getLoginUser(request);
        boolean result = userSubscriptionService.isSubscribed(loginUser.getId(), category);
        return ResultUtils.success(result);
    }
} 