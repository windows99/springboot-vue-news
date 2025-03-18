package com.guanzhi.springbootinit.model.dto.news;

import lombok.Data;

import java.io.Serializable;


@Data
public class UserNewsViewPK implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long newsId;

    // 无需其他字段
}