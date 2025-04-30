package com.guanzhi.springbootinit.model.dto.news;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 新闻推送配置数据传输对象
 */
@Data
public class NewsPushConfigDTO implements Serializable {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 新闻ID
     */
    private Long newsId;

    /**
     * 新闻标题
     */
    private String newsTitle;

    /**
     * 推送名称
     */
    private String pushName;

    /**
     * 推送类型（0-按间隔时间推送，1-按固定时间推送）
     */
    private Integer pushType;

    /**
     * 推送时间表达式
     */
    private String pushTimeExpression;

    /**
     * 推送状态（0-暂停，1-激活）
     */
    private Integer status;

    /**
     * 下次推送时间
     */
    private Date nextPushTime;

    /**
     * 推送目标（逗号分隔的目标列表，如"email,sms"）
     */
    private String pushTargets;

    /**
     * 推送内容模板
     */
    private String contentTemplate;

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