package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.NewsFeedback;
import com.guanzhi.springbootinit.model.vo.NewsFeedbackVO;

import java.util.List;

/**
 * 新闻反馈服务
 *
 * @author sk
 */
public interface NewsFeedbackService extends IService<NewsFeedback> {
    /**
     * 提交反馈
     *
     * @param feedback 反馈信息
     * @return 是否成功
     */
    boolean submitFeedback(NewsFeedback feedback);

    /**
     * 审核反馈
     *
     * @param feedbackId 反馈ID
     * @param approved 是否通过
     * @param reviewNotes 审核备注
     * @param reviewerId 审核人ID
     * @return 是否成功
     */
    boolean reviewFeedback(Long feedbackId, boolean approved, String reviewNotes, Long reviewerId);

    /**
     * 获取待审核反馈列表（分页）
     *
     * @param current 当前页
     * @param pageSize 每页大小
     * @return 反馈列表
     */
    Page<NewsFeedbackVO> getPendingFeedbackList(long current, long pageSize);
} 