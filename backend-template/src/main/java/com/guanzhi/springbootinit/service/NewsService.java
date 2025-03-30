package com.guanzhi.springbootinit.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.News;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guanzhi.springbootinit.model.dto.news.NewsQueryRequest;

import java.util.List;

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

    JSONObject getJisunews(String channel);

    List<News> getTop3NewsByViewCount();


    void setStatusNews(Long newsId, int statusInt);
}
