package com.guanzhi.springbootinit.service;

import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;

import java.util.List;

public interface NewsRecommendService {
    /**
     * 获取用户个性化推荐的新闻
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐的新闻列表
     */
    List<NewsRecommendDTO> getPersonalizedNews(Long userId, Integer limit);
    
    /**
     * 获取热门新闻
     * @param limit 推荐数量
     * @return 热门新闻列表
     */
    List<NewsRecommendDTO> getHotNews(Integer limit);
} 