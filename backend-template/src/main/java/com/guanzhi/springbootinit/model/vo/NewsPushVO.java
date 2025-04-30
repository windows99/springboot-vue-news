package com.guanzhi.springbootinit.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 新闻推送VO对象
 */
@Data
public class NewsPushVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 推送ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 新闻ID
     */
    private Long newsId;
    
    /**
     * 推送状态 0-未读 1-已读 2-失败
     */
    private Integer status;
    
    /**
     * 新闻标题
     */
    private String newsTitle;
    
    /**
     * 新闻内容
     */
    private String newsContent;
    
    /**
     * 新闻创建时间
     */
    private Date newsCreateTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 