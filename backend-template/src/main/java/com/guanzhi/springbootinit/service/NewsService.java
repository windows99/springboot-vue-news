package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.News;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guanzhi.springbootinit.model.dto.news.NewsQueryRequest;

public interface NewsService extends IService<News> {
//   List<News> getNewsList(Map<String, Object> params);

    // 根据用户ID查询用户信息
    News getNewsById(Long newsId);

    boolean addNews(News news);

    void updateNews(News news);

    void deleteNewsById(Long newsId);

    QueryWrapper<News> getQueryWrapper(NewsQueryRequest newsQueryRequest);

    void updateNewsStatus(Long newsId, Integer status);

    void publishNews(Long newsId);

    void shelfNews(Long newsId);

}
