package com.guanzhi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.entity.UserOperationLog;
import com.guanzhi.springbootinit.service.UserOperationLogService;
import com.guanzhi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户操作日志接口
 *
 * @author sk
 */
@RestController
@RequestMapping("/user/operation")
@Slf4j
public class UserOperationLogController {
    @Resource
    private UserOperationLogService userOperationLogService;
    @Resource
    private UserService userService;

    /**
     * 获取用户操作历史
     *
     * @param request 请求
     * @param limit 限制数量
     * @return 操作历史列表
     */
    @GetMapping("/history")
    public BaseResponse<List<UserOperationLog>> getOperationHistory(HttpServletRequest request,
            @RequestParam(defaultValue = "10") int limit) {
        User loginUser = userService.getLoginUser(request);
        List<UserOperationLog> history = userOperationLogService.getUserOperationHistory(loginUser.getId(), limit);
        return ResultUtils.success(history);
    }
} 