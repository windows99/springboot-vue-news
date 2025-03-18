package com.guanzhi.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.io.Serializable;

/**
 * 用户浏览新闻记录表(UserNewsView)实体类
 *
 * @author makejava
 * @since 2025-03-17 22:58:52
 */
@TableName(value = "user_news_view")
@Data
public class UserNewsView implements Serializable {
    private static final long serialVersionUID = 747261572504128794L;


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
     * 新闻标题
     */
    private String newsTitle;
    /**
     * 浏览时间
     */
    private LocalDateTime viewTime;


    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public void setViewTime(LocalDateTime viewTime) {
        this.viewTime = viewTime;
    }

    public String getNewsTitle() { return newsTitle; }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

}

