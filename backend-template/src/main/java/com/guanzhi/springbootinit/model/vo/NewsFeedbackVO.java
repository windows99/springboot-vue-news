package com.guanzhi.springbootinit.model.vo;

import com.guanzhi.springbootinit.model.entity.NewsFeedback;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 新闻反馈视图（脱敏）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NewsFeedbackVO extends NewsFeedback {
    /**
     * 新闻标题
     */
    private String newsTitle;

    /**
     * 反馈人用户名
     */
    private String username;
} 