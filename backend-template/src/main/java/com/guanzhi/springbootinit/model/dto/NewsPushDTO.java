package com.guanzhi.springbootinit.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 新闻推送数据传输对象
 */
@Data
public class NewsPushDTO implements Serializable {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 新闻ID
     */
    private String newsId;
    
    /**
     * 新闻标题
     */
    private String title;
    
    /**
     * 新闻内容
     */
    private String content;
    
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 新闻来源
     */
    private String source;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 发布时间
     */
    private Date publishTime;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 点赞数
     */
    private Integer likeCount;
    
    /**
     * 评论数
     */
    private Integer commentCount;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 新闻状态
     */
    private Integer status;
    
    /**
     * 是否删除
     */
    private Integer isDelete;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    private static final long serialVersionUID = 1L;
} 