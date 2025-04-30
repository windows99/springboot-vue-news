package com.guanzhi.springbootinit.model.dto.news;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 新闻推送DTO对象
 */
@Data
public class NewsPushDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 推送ID */
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String userName;

    /** 新闻ID */
    private Long newsId;

    /** 新闻标题 */
    private String newsTitle;

    /** 新闻封面图片 */
    private String newsCoverImage;

    /** 推送时间 */
    private Date pushTime;

    /** 推送类型 1-即时 2-定时 */
    private Integer pushType;

    /** 关联的推送配置ID */
    private Long configId;

    /** 状态 0-待推送 1-已推送 2-推送失败 */
    private Integer status;

    /** 是否已读 0-未读 1-已读 */
    private Integer isRead;

    /** 阅读时间 */
    private Date readTime;

    /** 创建时间 */
    private Date createTime;
}