package com.guanzhi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.annotation.AuthCheck;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.constant.UserConstant;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.news.NewsFeedbackRequest;
import com.guanzhi.springbootinit.model.dto.news.NewsFeedbackReviewRequest;
import com.guanzhi.springbootinit.model.entity.NewsFeedback;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.vo.NewsFeedbackVO;
import com.guanzhi.springbootinit.service.NewsFeedbackService;
import com.guanzhi.springbootinit.service.UserOperationLogService;
import com.guanzhi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 新闻反馈接口
 *
 * @author sk
 */
@RestController
@RequestMapping("/news/feedback")
@Slf4j
public class NewsFeedbackController {
    @Resource
    private NewsFeedbackService newsFeedbackService;
    @Resource
    private UserOperationLogService userOperationLogService;
    @Resource
    private UserService userService;

    /**
     * 提交反馈
     *
     * @param request 反馈请求
     * @param httpRequest HTTP请求
     * @return 是否成功
     */
    @PostMapping("/submit")
    public BaseResponse<Boolean> submitFeedback(@RequestBody NewsFeedbackRequest request, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        
        NewsFeedback feedback = new NewsFeedback();
        feedback.setUserId(loginUser.getId());
        feedback.setNewsId(request.getNewsId());
        feedback.setContent(request.getContent());
        
        boolean result = newsFeedbackService.submitFeedback(feedback);
        if (result) {
            // 记录操作日志
            userOperationLogService.logOperation(
                loginUser.getId(),
                "FEEDBACK",
                feedback.getNewsId(),
                "NEWS",
                "提交新闻反馈：" + request.getContent()
            );
        }
        return ResultUtils.success(result);
    }

    /**
     * 审核反馈
     *
     * @param request 审核请求
     * @param httpRequest HTTP请求
     * @return 是否成功
     */
    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> reviewFeedback(@RequestBody NewsFeedbackReviewRequest request, HttpServletRequest httpRequest) {
        User loginUser = userService.getLoginUser(httpRequest);
        boolean result = newsFeedbackService.reviewFeedback(
            request.getFeedbackId(),
            request.isApproved(),
            request.getReviewNotes(),
            loginUser.getId()
        );
        return ResultUtils.success(result);
    }

    /**
     * 获取待审核反馈列表
     *
     * @param current 当前页
     * @param pageSize 每页大小
     * @return 反馈列表
     */
    @GetMapping("/pending")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<NewsFeedbackVO>> getPendingFeedbackList(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        Page<NewsFeedbackVO> page = newsFeedbackService.getPendingFeedbackList(current, pageSize);
        return ResultUtils.success(page);
    }

    /**
     * 获取我的反馈列表
     *
     * @param request 请求
     * @return 反馈列表
     */
    @GetMapping("/list")
    public BaseResponse<List<NewsFeedback>> getMyFeedbackList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<NewsFeedback> list = newsFeedbackService.lambdaQuery()
                .eq(NewsFeedback::getUserId, loginUser.getId())
                .eq(NewsFeedback::getIsDelete, 0)
                .orderByDesc(NewsFeedback::getCreateTime)
                .list();
        return ResultUtils.success(list);
    }

    /**
     * 获取反馈详情
     *
     * @param id 反馈ID
     * @param request 请求
     * @return 反馈详情
     */
    @GetMapping("/detail/{id}")
    public BaseResponse<NewsFeedback> getFeedbackDetail(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        NewsFeedback feedback = newsFeedbackService.lambdaQuery()
                .eq(NewsFeedback::getId, id)
                .eq(NewsFeedback::getUserId, loginUser.getId())
                .eq(NewsFeedback::getIsDelete, 0)
                .one();
        if (feedback == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "反馈不存在");
        }
        return ResultUtils.success(feedback);
    }

    /**
     * 删除反馈
     *
     * @param id 反馈ID
     * @param request 请求
     * @return 是否成功
     */
    @PostMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteFeedback(@PathVariable Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = newsFeedbackService.lambdaUpdate()
                .eq(NewsFeedback::getId, id)
                .eq(NewsFeedback::getUserId, loginUser.getId())
                .set(NewsFeedback::getIsDelete, 1)
                .update();
        return ResultUtils.success(result);
    }
} 