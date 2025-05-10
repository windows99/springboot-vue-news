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
    public BaseResponse<Page<UserOperationLog>> getOperationHistory(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String targetType,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        try {
            Page<UserOperationLog> page = userOperationLogService.getOperationHistory(userId, operationType, targetType, current, pageSize);
            return ResultUtils.success(page);
        } catch (Exception e) {
            log.error("获取操作历史失败", e);
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "获取操作历史失败");
        }
    }
} 