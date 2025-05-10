package com.guanzhi.springbootinit.model.vo;

import com.guanzhi.springbootinit.model.entity.NewsPush;
import lombok.Data;

import java.io.Serializable;

/**
 * 新闻推送视图对象
 */
@Data
public class NewsPushVO implements Serializable {
    
    /**
     * 推送记录
     */
    private NewsPush newsPush;
    
    /**
     * 新闻标题
     */
    private String newsTitle;
    
    /**
     * 新闻封面图
     */
    private String newsCoverImage;
    
    private static final long serialVersionUID = 1L;
} 
 
 