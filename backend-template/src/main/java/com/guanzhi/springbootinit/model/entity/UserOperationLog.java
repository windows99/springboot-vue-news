package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户操作日志
 *
 * @author sk
 */
@Data
@TableName("user_operation_log")
public class UserOperationLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 操作类型：VIEW-浏览、LIKE-点赞、COMMENT-评论、FEEDBACK-反馈、SUBSCRIBE-订阅
     */
    private String operationType;

    /**
     * 操作目标ID
     */
    private Long targetId;

    /**
     * 操作目标类型：NEWS-新闻、TAG-标签、COMMENT-评论
     */
    private String targetType;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 操作详情
     */
    private String operationDetail;

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