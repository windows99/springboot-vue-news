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
import org.jetbrains.annotations.NotNull;
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
    public Page<News> getPersonalizedNewsPage(Long userId, long current, long pageSize) {
        // 获取所有状态为3的新闻总数
        LambdaQueryWrapper<News> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(News::getStatus, 3).eq(News::getIsdelete, 0);  // 只统计状态为3的新闻总数
        long total = newsMapper.selectCount(countWrapper);
        
        // 检查用户是否有浏览历史
        boolean hasUserHistory = hasUserHistory(userId);
        
        // 如果用户无历史数据，混合推荐热门和最新内容
        if (!hasUserHistory) {
            return getMixedRecommendations(current, pageSize);
        }
        
        // 获取用户的兴趣标签
        List<Long> userSubscriptions = getUserSubscriptions(userId);
        
        // 获取用户的浏览历史
        List<Long> userViewedNewsIds = getUserViewedNewsIds(userId);
        
        // 获取用户浏览过的新闻分类
        List<Long> userViewedCategories = getUserViewedCategories(userId);

        // 获取相似用户喜欢的新闻（协同过滤部分）
        List<Long> similarUserNewsIds = new ArrayList<>();
        if (userViewedNewsIds.size() > 5) { // 只有当用户有足够的历史记录时才使用协同过滤
            similarUserNewsIds = getSimilarUsersPreferredNewsIds(userId, 10);
        }
        
        // 构建分页查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)  // 状态为3的新闻
                   .eq(News::getIsdelete, 0) // 未删除的新闻
                   .orderByDesc(News::getCreatetime); // 按创建时间降序
        
        // 执行分页查询
        Page<News> newsPage = newsMapper.selectPage(new Page<>(current, pageSize * 2), queryWrapper); // 获取更多候选项以便排序
        
        // 获取所有新闻
        List<News> allNews = newsPage.getRecords();
        
        // 根据用户兴趣和浏览历史对新闻进行排序
        List<News> sortedNews = sortNewsByUserPreferences(allNews, userSubscriptions, userViewedCategories, userViewedNewsIds, similarUserNewsIds);
        
        // 限制结果数量
        if (sortedNews.size() > pageSize) {
            sortedNews = sortedNews.subList(0, (int)pageSize);
        }
        
        // 构建返回结果
        Page<News> resultPage = new Page<>();
        resultPage.setTotal(total);
        resultPage.setCurrent(current);
        resultPage.setSize(pageSize);
        resultPage.setRecords(sortedNews);
        
        return resultPage;
    }

    /**
     * 检查用户是否有浏览历史
     */
    private boolean hasUserHistory(Long userId) {
        LambdaQueryWrapper<UserNewsView> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserNewsView::getUserId, userId);
        return userNewsViewMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 为新用户提供混合推荐（热门+最新）
     */
    private Page<News> getMixedRecommendations(long current, long pageSize) {
        // 获取一半热门新闻
        int halfSize = (int) (pageSize / 2);
        List<News> hotNews = getHotNews(halfSize);
        
        // 获取一半最新新闻
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)
                   .eq(News::getIsdelete, 0)
                   .orderByDesc(News::getCreatetime)
                   .last("LIMIT " + halfSize);
        
        List<News> latestNews = newsMapper.selectList(queryWrapper);
        
        // 合并结果
        List<News> mixedResults = new ArrayList<>();
        mixedResults.addAll(hotNews);
        // 过滤掉已经在热门列表中的新闻
        Set<Long> hotNewsIds = hotNews.stream()
                .map(News::getId)
                .collect(Collectors.toSet());
        
        latestNews.stream()
                .filter(news -> !hotNewsIds.contains(news.getId()))
                .forEach(mixedResults::add);
        
        // 如果总数超过pageSize，截取前pageSize条
        if (mixedResults.size() > pageSize) {
            mixedResults = mixedResults.subList(0, (int)pageSize);
        }
        
        // 构建返回结果
        Page<News> resultPage = new Page<>();
        resultPage.setTotal(mixedResults.size());
        resultPage.setCurrent(current);
        resultPage.setSize(pageSize);
        resultPage.setRecords(mixedResults);
        
        return resultPage;
    }

    /**
     * 获取与当前用户相似的用户喜欢的新闻ID列表
     */
    private List<Long> getSimilarUsersPreferredNewsIds(Long userId, int limit) {
        // 获取当前用户的分类偏好
        List<Long> userCategories = getUserViewedCategories(userId);
        
        if (userCategories.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 找到浏览过相似分类的其他用户
        List<Long> similarUserIds = userNewsViewMapper.findUsersBySimilarCategories(userCategories, userId, 10);
        
        if (similarUserIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 获取这些用户最近浏览的新闻，但当前用户未浏览过的
        List<Long> viewedNewsIds = getUserViewedNewsIds(userId);
        List<Long> recommendedNewsIds = userNewsViewMapper.getNewsViewedBySimilarUsers(similarUserIds, viewedNewsIds, limit);
        
        return recommendedNewsIds;
    }

    @Override
    public Page<News> getHotNewsPage(long current, long pageSize) {
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
        
        return newsPage;
    }

    @Override
    public List<News> getPersonalizedNews(Long userId, Integer limit) {
        // 检查用户是否有浏览历史
        boolean hasUserHistory = hasUserHistory(userId);
        
        // 如果用户无历史数据，返回热门新闻
        if (!hasUserHistory) {
            return getHotNews(limit);
        }
        
        // 获取用户的兴趣标签
        List<Long> userSubscriptions = getUserSubscriptions(userId);
        
        // 获取用户的浏览历史
        List<Long> userViewedNewsIds = getUserViewedNewsIds(userId);
        
        // 获取用户浏览过的新闻分类
        List<Long> userViewedCategories = getUserViewedCategories(userId);
        
        // 获取相似用户喜欢的新闻
        List<Long> similarUserNewsIds = new ArrayList<>();
        if (userViewedNewsIds.size() > 5) {
            similarUserNewsIds = getSimilarUsersPreferredNewsIds(userId, 10);
        }
        
        // 构建查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)
                   .eq(News::getIsdelete, 0)
                   .orderByDesc(News::getCreatetime);
        
        // 执行查询
        List<News> allNews = newsMapper.selectList(queryWrapper);
        
        // 根据用户兴趣和浏览历史对新闻进行排序
        List<News> sortedNews = sortNewsByUserPreferences(allNews, userSubscriptions, userViewedCategories, userViewedNewsIds, similarUserNewsIds);
        
        // 限制数量
        if (sortedNews.size() > limit) {
            sortedNews = sortedNews.subList(0, limit);
        }
        
        return sortedNews;
    }

    @Override
    public List<News> getHotNews(Integer limit) {
        // 构建查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)
                   .eq(News::getIsdelete, 0)
                   .orderByDesc(News::getViewcount)
                   .orderByDesc(News::getCreatetime)
                   .last("LIMIT " + limit);
        
        // 执行查询
        return newsMapper.selectList(queryWrapper);
    }

    /**
     * 获取用户的订阅分类
     */
    private @NotNull List<Long> getUserSubscriptions(Long userId) {
        try {
            LambdaQueryWrapper<UserSubscription> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserSubscription::getUserId, userId)
                       .eq(UserSubscription::getStatus, 1) // 1-订阅中
                       .eq(UserSubscription::getIsDelete, 0);
            
            List<UserSubscription> subscriptions = userSubscriptionMapper.selectList(queryWrapper);
            return subscriptions.stream()
                    .map(UserSubscription::getCategory)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户订阅信息失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * 获取用户浏览过的新闻ID列表
     */
    private List<Long> getUserViewedNewsIds(Long userId) {
        try {
            LambdaQueryWrapper<UserNewsView> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserNewsView::getUserId, userId)
                       .orderByDesc(UserNewsView::getViewTime);
            
            List<UserNewsView> views = userNewsViewMapper.selectList(queryWrapper);
            return views.stream()
                    .map(UserNewsView::getNewsId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户浏览历史失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * 获取用户浏览过的新闻分类
     */
    private List<Long> getUserViewedCategories(Long userId) {
        try {
            // 使用UserNewsViewMapper中的方法获取用户浏览的新闻类别
            List<Map<String, Object>> categoryPreferences = userNewsViewMapper.getUserCategoryPreferences(userId);
            
            return categoryPreferences.stream()
                    .map(map -> {
                        Object category = map.get("category");
                        return category instanceof Number ? ((Number) category).longValue() : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户分类偏好失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
    
    /**
     * 根据用户兴趣和浏览历史对新闻进行排序
     * 采用多维度的评分机制实现个性化排序
     */
    private List<News> sortNewsByUserPreferences(List<News> newsList, 
                                               List<Long> userSubscriptions,
                                               List<Long> userViewedCategories, 
                                               List<Long> userViewedNewsIds,
                                               List<Long> similarUserNewsIds) {
        // 计算每条新闻的得分
        Map<News, Double> newsScores = new HashMap<>();
        
        // 获取当前时间用于计算时效性
        long currentTimeMillis = System.currentTimeMillis();
        
        for (News news : newsList) {
            double score = 0.0;
            
            // 1. 订阅匹配度得分 - 用户明确订阅的分类权重最高
            if (userSubscriptions.contains(String.valueOf(news.getCategory()))) {
                score += 5.0; 
            }
            
            // 2. 浏览历史匹配度得分 - 用户曾经浏览过的分类权重较高
            if (userViewedCategories.contains(news.getCategory())) {
                score += 3.0; 
            }
            
            // 3. 协同过滤得分 - 相似用户喜欢的新闻权重中等
            if (similarUserNewsIds.contains(news.getId())) {
                score += 2.5;
            }
            
            // 4. 内容流行度得分 - 流行内容一般较有价值
            score += Math.log10(news.getViewcount() + 1) * 0.5; // 使用对数函数平滑化，避免高流行度项目过度主导
            score += Math.log10(news.getLikeCount() + 1) * 0.8; // 点赞比浏览更能表达质量
            score += Math.log10(news.getCommentCount() + 1) * 0.6; // 评论互动也是重要指标
            
            // 5. 时效性得分 - 新鲜内容权重较高
            if (news.getCreatetime() != null) {
                long newsAgeMillis = currentTimeMillis - news.getCreatetime().getTime();
                double daysOld = newsAgeMillis / (1000.0 * 60 * 60 * 24);
                // 新闻越新，得分越高，呈指数衰减
                score += Math.max(0, 3.0 * Math.exp(-0.1 * daysOld)); // 30天后时效性得分接近0
            }
            
            // 6. 去重处理 - 已浏览过的内容显著降低权重
            if (userViewedNewsIds.contains(news.getId())) {
                score *= 0.2; // 降低80%的权重，但不完全排除
            }
            
            // 存储最终得分
            newsScores.put(news, score);
        }
        
        // 根据得分对新闻进行排序（从高到低）
        return newsList.stream()
                .sorted((n1, n2) -> Double.compare(newsScores.getOrDefault(n2, 0.0), 
                                                  newsScores.getOrDefault(n1, 0.0)))
                .collect(Collectors.toList());
    }
} 