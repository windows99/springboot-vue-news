package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * 评论(Comment)实体类
 *
 * @author makejava
 * @since 2025-03-06 10:40:36
 */
@TableName(value = "comment")
@Data
public class Comment implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 评论内容
     */
    private String content;

    /**
     * 新闻标题
     */
    private String newsTitle;
    /**
     * 新闻id
     */
    private Long newsId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 父评论id
     */
    private Long parentId;
    /**
     * 点赞数
     */
    private Integer likeNum;
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
    @TableLogic
    private Integer isDelete;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    /**
     * 评论的用户名
     */
    @TableField(exist = false)
    private String username;
    /**
     * 评论的用户头像
     */
    @TableField(exist = false)
    private String userAvatar;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    public String getNewsTitle() { return newsTitle; }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }
}

