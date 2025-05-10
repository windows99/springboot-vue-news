package com.guanzhi.springbootinit.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.News;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guanzhi.springbootinit.model.dto.news.NewsQueryRequest;
import com.guanzhi.springbootinit.model.dto.news.NewsAddRequest;
import com.guanzhi.springbootinit.model.dto.news.NewsUpdateRequest;

import java.util.List;

public interface NewsService extends IService<News> {
//   List<News> getNewsList(Map<String, Object> params);

    // 根据用户ID查询用户信息
    News getNewsById(Long newsId);

    /**
     * 添加新闻
     *
     * @param newsAddRequest 新闻添加请求
     * @return 新闻ID
     */
    Long addNews(NewsAddRequest newsAddRequest);

    /**
     * 更新新闻
     *
     * @param newsUpdateRequest 新闻更新请求
     * @return 是否更新成功
     */
    boolean updateNews(NewsUpdateRequest newsUpdateRequest);

    /**
     * 删除新闻
     *
     * @param id 新闻ID
     * @param userId 操作用户ID
     * @return 是否删除成功
     */
    boolean deleteNewsById(Long id, Long userId);

    QueryWrapper<News> getQueryWrapper(NewsQueryRequest newsQueryRequest);

    void updateNewsStatus(Long newsId, Integer status);

    /**
     * 发布新闻
     *
     * @param newsId 新闻ID
     * @param userId 操作用户ID
     */
    void publishNews(Long newsId, Long userId);

    /**
     * 下架新闻
     *
     * @param newsId 新闻ID
     * @param userId 操作用户ID
     */
    void shelfNews(Long newsId, Long userId);

    JSONObject getJisunews(String channel);

    List<News> getTop3NewsByViewCount();

    /**
     * 设置新闻状态
     *
     * @param newsId 新闻ID
     * @param statusInt 状态值
     * @param userId 操作用户ID
     */
    void setStatusNews(Long newsId, int statusInt, Long userId);
}
