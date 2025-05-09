package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.model.entity.News;

import java.util.List;

public interface NewsRecommendService {
    /**
     * 获取用户个性化推荐新闻（分页）
     * @param userId 用户ID
     * @param current 当前页码
     * @param pageSize 每页大小
     * @return 推荐新闻分页数据
     */
    Page<News> getPersonalizedNewsPage(Long userId, long current, long pageSize);
    
    /**
     * 获取热门新闻（分页）
     * @param current 当前页码
     * @param pageSize 每页大小
     * @return 热门新闻分页数据
     */
    Page<News> getHotNewsPage(long current, long pageSize);
    
    /**
     * 获取用户个性化推荐新闻列表（用于推送）
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 推荐新闻列表
     */
    List<News> getPersonalizedNews(Long userId, Integer limit);
    
    /**
     * 获取热门新闻列表（用于推送）
     * @param limit 限制数量
     * @return 热门新闻列表
     */
    List<News> getHotNews(Integer limit);
} 