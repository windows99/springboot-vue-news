package com.guanzhi.springbootinit.model.dto.news;

import com.guanzhi.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新闻查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NewsQueryRequest extends PageRequest {
    /**
     * 新闻ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 分类
     */
    private Long category;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 作者
     */
    private String author;

    /**
     * 来源
     */
    private String source;
}
