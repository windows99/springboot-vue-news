package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 新闻推送记录实体
 */
@TableName(value = "news_push")
@Data
public class NewsPush implements Serializable {
    
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
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
     * 推送时间
     */
    private Date pushTime;
    
    /**
     * 推送类型 1-即时推送 2-定时推送
     */
    private Integer pushType;
    
    /**
     * 推送状态 0-待推送 1-已推送 2-推送失败
     */
    private Integer status;
    
    /**
     * 是否已读 0-未读 1-已读
     */
    private Integer isRead;
    
    /**
     * 阅读时间
     */
    private Date readTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 是否删除
     */
    private Integer isDelete;
    
    private static final long serialVersionUID = 1L;
} 