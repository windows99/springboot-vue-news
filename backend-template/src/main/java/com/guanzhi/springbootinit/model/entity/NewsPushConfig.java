package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 新闻推送配置实体类
 */
@TableName(value = "news_push_config")
@Data
public class NewsPushConfig implements Serializable {

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
     * 推送名称
     */
    private String pushName;

    /**
     * 推送类型（0-按间隔时间推送，1-按固定时间推送）
     */
    private Integer pushType;

    /**
     * 推送时间表达式
     * 对于类型0：单位为分钟的间隔时间，如30表示每30分钟推送一次
     * 对于类型1：cron表达式，如"0 0 8 * * ?"表示每天早上8点推送
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
     * 最后执行时间
     */
    private Date lastExecuteTime;

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

    /**
     * 是否删除(0-未删, 1-已删)
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 