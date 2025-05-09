package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * 新闻(News)实体类
 *
 * @author makejava
 * @since 2025-03-05 18:56:04
 */
@TableName(value = "news")
@Data
public class News implements Serializable {


    @TableField(exist = false)
    private static final long serialVersionUID = -27934559206686464L;
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 封面图片
     */
    @TableField("coverimage")
    private String coverImage;

    /**
     * 来源
     */
    private String source;
    /**
     * 来源链接
     */
    private String sourceUrl;
    /**
     * 作者
     */
    private String author;
    /**
     * 分类
     */
    private Long category;
    /**
     * 备注
     */
    private String notes;
    /**
     * 状态 0-草稿 1-已发布
     */
    private Integer status;
    /**
     * 浏览量
     */
    @TableField("viewCount")
    private Integer viewcount;
    /**
     * 点赞数
     */
    private Integer likeCount;
    /**
     * 评论数
     */
    private Integer commentCount;

    @TableField("createTime")
    private Date createtime;
    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField("isDelete")
    private Integer isdelete;


}

