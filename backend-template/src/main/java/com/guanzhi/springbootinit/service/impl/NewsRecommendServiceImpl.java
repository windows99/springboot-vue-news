package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.NewsTagMapper;
import com.guanzhi.springbootinit.mapper.UserNewsViewMapper;
import com.guanzhi.springbootinit.mapper.UserSubscriptionMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsTag;
import com.guanzhi.springbootinit.model.entity.UserNewsView;
import com.guanzhi.springbootinit.model.entity.UserSubscription;
import com.guanzhi.springbootinit.service.NewsRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsRecommendServiceImpl implements NewsRecommendService {

    @Resource
    private UserNewsViewMapper userNewsViewMapper;
    
    @Resource
    private NewsMapper newsMapper;
    
    @Resource
    private NewsTagMapper newsTagMapper;
    
    @Resource
    private UserSubscriptionMapper userSubscriptionMapper;

    @Override
    public Page<NewsRecommendDTO> getPersonalizedNewsPage(Long userId, long current, long pageSize) {
        // 获取所有状态为3的新闻总数
        LambdaQueryWrapper<News> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(News::getStatus, 3).eq(News::getIsdelete, 0);  // 只统计状态为3的新闻总数
        long total = newsMapper.selectCount(countWrapper);
        
        // 获取用户的兴趣标签
        List<String> userSubscriptions = getUserSubscriptions(userId);
        
        // 获取用户的浏览历史
        List<Long> userViewedNewsIds = getUserViewedNewsIds(userId);
        
        // 获取用户浏览过的新闻分类
        List<Long> userViewedCategories = getUserViewedCategories(userId);
        
        // 构建分页查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)  // 状态为3的新闻
                   .eq(News::getIsdelete, 0) // 未删除的新闻
                   .orderByDesc(News::getCreatetime); // 按创建时间降序
        
        // 执行分页查询
        Page<News> newsPage = newsMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
        
        // 获取所有新闻
        List<News> allNews = newsPage.getRecords();
        
        // 根据用户兴趣和浏览历史对新闻进行排序
        List<News> sortedNews = sortNewsByUserPreferences(allNews, userSubscriptions, userViewedCategories, userViewedNewsIds);
        
        // 转换为DTO
        Page<NewsRecommendDTO> dtoPage = new Page<>();
        dtoPage.setTotal(total);
        dtoPage.setCurrent(current);
        dtoPage.setSize(pageSize);
        
        List<NewsRecommendDTO> dtoList = sortedNews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }

    @Override
    public Page<NewsRecommendDTO> getHotNewsPage(long current, long pageSize) {
        // 获取所有状态为3的新闻总数
        LambdaQueryWrapper<News> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(News::getStatus, 3).eq(News::getIsdelete, 0);
        long total = newsMapper.selectCount(countWrapper);
        
        // 构建分页查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)
                   .eq(News::getIsdelete, 0)
                   .orderByDesc(News::getViewcount) // 按浏览量降序
                   .orderByDesc(News::getCreatetime); // 再按创建时间降序
        
        // 执行分页查询
        Page<News> newsPage = newsMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
        
        // 转换为DTO
        Page<NewsRecommendDTO> dtoPage = new Page<>();
        dtoPage.setTotal(total);
        dtoPage.setCurrent(current);
        dtoPage.setSize(pageSize);
        
        List<NewsRecommendDTO> dtoList = newsPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }

    @Override
    public List<NewsRecommendDTO> getPersonalizedNews(Long userId, Integer limit) {
        // 获取用户的兴趣标签
        List<String> userSubscriptions = getUserSubscriptions(userId);
        
        // 获取用户的浏览历史
        List<Long> userViewedNewsIds = getUserViewedNewsIds(userId);
        
        // 获取用户浏览过的新闻分类
        List<Long> userViewedCategories = getUserViewedCategories(userId);
        
        // 构建查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)
                   .eq(News::getIsdelete, 0)
                   .orderByDesc(News::getCreatetime);
        
        // 执行查询
        List<News> allNews = newsMapper.selectList(queryWrapper);
        
        // 根据用户兴趣和浏览历史对新闻进行排序
        List<News> sortedNews = sortNewsByUserPreferences(allNews, userSubscriptions, userViewedCategories, userViewedNewsIds);
        
        // 限制数量
        if (sortedNews.size() > limit) {
            sortedNews = sortedNews.subList(0, limit);
        }
        
        // 转换为DTO
        return sortedNews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsRecommendDTO> getHotNews(Integer limit) {
        // 构建查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)
                   .eq(News::getIsdelete, 0)
                   .orderByDesc(News::getViewcount)
                   .orderByDesc(News::getCreatetime)
                   .last("LIMIT " + limit);
        
        // 执行查询
        List<News> newsList = newsMapper.selectList(queryWrapper);
        
        // 转换为DTO
        return newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将News实体转换为NewsRecommendDTO
     */
    private NewsRecommendDTO convertToDTO(News news) {
        NewsRecommendDTO dto = new NewsRecommendDTO();
        BeanUtils.copyProperties(news, dto);
        
        // 设置分类名称
        if (news.getCategory() != null) {
            // 这里可以根据需要查询分类名称
            // 简化处理，直接使用分类ID作为名称
            dto.setCategoryName("分类" + news.getCategory());
        }
        
        return dto;
    }
    
    /**
     * 获取用户的订阅分类
     */
    private List<String> getUserSubscriptions(Long userId) {
        LambdaQueryWrapper<UserSubscription> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSubscription::getUserId, userId)
                   .eq(UserSubscription::getStatus, 1) // 1-订阅中
                   .eq(UserSubscription::getIsDelete, 0);
        
        List<UserSubscription> subscriptions = userSubscriptionMapper.selectList(queryWrapper);
        return subscriptions.stream()
                .map(UserSubscription::getCategory)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户浏览过的新闻ID列表
     */
    private List<Long> getUserViewedNewsIds(Long userId) {
        LambdaQueryWrapper<UserNewsView> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserNewsView::getUserId, userId)
                   .orderByDesc(UserNewsView::getViewTime);
        
        List<UserNewsView> views = userNewsViewMapper.selectList(queryWrapper);
        return views.stream()
                .map(UserNewsView::getNewsId)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取用户浏览过的新闻分类
     */
    private List<Long> getUserViewedCategories(Long userId) {
        // 使用UserNewsViewMapper中的方法获取用户浏览的新闻类别
        List<Map<String, Object>> categoryPreferences = userNewsViewMapper.getUserCategoryPreferences(userId);
        
        return categoryPreferences.stream()
                .map(map -> {
                    Object category = map.get("category");
                    return category instanceof Number ? ((Number) category).longValue() : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据用户兴趣和浏览历史对新闻进行排序
     */
    private List<News> sortNewsByUserPreferences(List<News> newsList, List<String> userSubscriptions, 
                                                List<Long> userViewedCategories, List<Long> userViewedNewsIds) {
        // 计算每条新闻的得分
        Map<News, Double> newsScores = new HashMap<>();
        
        for (News news : newsList) {
            double score = 0.0;
            
            // 1. 根据用户订阅分类计算得分
            if (userSubscriptions.contains(String.valueOf(news.getCategory()))) {
                score += 3.0; // 用户订阅的分类，权重较高
            }
            
            // 2. 根据用户浏览历史计算得分
            if (userViewedCategories.contains(news.getCategory())) {
                score += 2.0; // 用户浏览过的分类，权重中等
            }
            
            // 3. 排除用户已浏览过的新闻
            if (userViewedNewsIds.contains(news.getId())) {
                score -= 5.0; // 用户已浏览过的新闻，大幅降低权重
            }
            
            // 4. 根据新闻浏览量计算得分
            score += Math.log1p(news.getViewcount()) * 0.5; // 使用对数函数平滑浏览量影响
            
            // 5. 根据新闻创建时间计算得分（越新的新闻得分越高）
            long now = System.currentTimeMillis();
            long newsTime = news.getCreatetime().getTime();
            long diffHours = (now - newsTime) / (1000 * 60 * 60);
            score += Math.max(0, 24 - diffHours) * 0.1; // 24小时内的新闻获得额外加分
            
            newsScores.put(news, score);
        }
        
        // 根据得分对新闻进行排序
        return newsList.stream()
                .sorted((n1, n2) -> Double.compare(newsScores.getOrDefault(n2, 0.0), newsScores.getOrDefault(n1, 0.0)))
                .collect(Collectors.toList());
    }
} 