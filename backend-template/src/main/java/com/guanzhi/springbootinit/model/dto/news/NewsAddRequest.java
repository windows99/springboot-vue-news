package com.guanzhi.springbootinit.model.dto.news;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 新闻添加请求
 */
@Data
public class NewsAddRequest implements Serializable {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 新闻内容
     */
    private String content;

    /**
     * 新闻分类
     */
    private Long category;

    /**
     * 新闻状态（0-草稿，1-待审核，2-审核中，3-已发布，4-审核失败，5-已下架，6-反馈新闻）
     */
    private Integer status;

    /**
     * 新闻作者
     */
    private String author;

    /**
     * 新闻来源
     */
    private String source;

    /**
     * 新闻封面图片
     */
    private String coverImage;

    /**
     * 新闻标签ID列表
     */
    private List<Long> tagIds;

    private static final long serialVersionUID = 1L;
} 