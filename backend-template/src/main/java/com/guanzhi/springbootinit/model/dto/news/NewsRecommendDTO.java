package com.guanzhi.springbootinit.model.dto.news;

import lombok.Data;

import java.util.Date;

@Data
public class NewsRecommendDTO {
    private Long id;
    private String title;
    private String coverimage;
    private String content;
    private Long category;
    private String categoryName;
    private Integer viewcount;
    private Date createtime;
} 