package com.guanzhi.springbootinit.model.dto.news;

import lombok.Data;

import java.util.Date;

@Data
public class NewsRecommendDTO {
    private Long id;
    private String title;
    private String coverImage;
    private Long category;
    private String categoryName;
    private Integer viewCount;
    private Date createTime;
} 