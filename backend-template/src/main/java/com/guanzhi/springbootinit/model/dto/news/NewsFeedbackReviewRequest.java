package com.guanzhi.springbootinit.model.dto.news;

import lombok.Data;

/**
 * 新闻反馈审核请求
 *
 * @author sk
 */
@Data
public class NewsFeedbackReviewRequest {
    /**
     * 反馈ID
     */
    private Long feedbackId;

    /**
     * 是否通过
     */
    private boolean approved;

    /**
     * 审核备注
     */
    private String reviewNotes;
} 