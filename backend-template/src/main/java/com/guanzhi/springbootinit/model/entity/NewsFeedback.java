package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 新闻反馈
 *
 * @author sk
 */
@Data
@TableName("news_feedback")
public class NewsFeedback implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 反馈用户ID
     */
    private Long userId;

    /**
     * 关联新闻ID
     */
    private Long newsId;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 反馈内容
     */
    private String content;


    /**
     * 审核备注
     */
    private String reviewNotes;

    /**
     * 审核时间
     */
    private Date reviewTime;

    /**
     * 审核人ID
     */
    private Long reviewerId;

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
} 