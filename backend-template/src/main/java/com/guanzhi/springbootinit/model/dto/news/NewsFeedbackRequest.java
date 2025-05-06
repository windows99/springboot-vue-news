package com.guanzhi.springbootinit.model.dto.news;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新闻反馈请求
 *
 * @author sk
 */
@Data
public class NewsFeedbackRequest {
    /**
     * 新闻ID
     */
    @NotNull(message = "新闻ID不能为空")
    private Long newsId;

    /**
     * 反馈内容
     */
    @NotBlank(message = "反馈内容不能为空")
    private String content;
} 