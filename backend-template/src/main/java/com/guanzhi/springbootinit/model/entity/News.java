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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverimage() {
        return coverImage;
    }

    public void setCoverimage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceurl() {
        return sourceUrl;
    }

    public void setSourceurl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getViewcount() {
        return viewcount;
    }

    public void setViewcount(Integer viewcount) {
        this.viewcount = viewcount;
    }

    public Integer getLikecount() {
        return likeCount;
    }

    public void setLikecount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentcount() {
        return commentCount;
    }

    public void setCommentcount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updateTime;
    }

    public void setUpdatetime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(Integer isdelete) {
        this.isdelete = isdelete;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


}

