package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.NewsTagMapper;
import com.guanzhi.springbootinit.mapper.UserNewsViewMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsTag;
import com.guanzhi.springbootinit.model.entity.UserNewsView;
import com.guanzhi.springbootinit.service.NewsRecommendService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsRecommendServiceImpl implements NewsRecommendService {

    private static final Logger log = LoggerFactory.getLogger(NewsRecommendServiceImpl.class);

    @Resource
    private UserNewsViewMapper userNewsViewMapper;
    
    @Resource
    private NewsMapper newsMapper;
    
    @Resource
    private NewsTagMapper newsTagMapper;

    @Override
    public List<NewsRecommendDTO> getPersonalizedNews(Long userId, Integer limit) {
        // 添加日志
        log.info("开始为用户 {} 生成个性化推荐，限制数量: {}", userId, limit);
        
        // 1. 获取用户的浏览记录
        List<UserNewsView> userViews = userNewsViewMapper.getUserRecentViews(userId, 50);
        log.info("用户浏览记录数量: {}", userViews != null ? userViews.size() : 0);
        
        // 如果用户没有浏览记录，返回热门新闻
        if (userViews == null || userViews.isEmpty()) {
            log.info("用户没有浏览记录，返回热门新闻");
            return getHotNews(limit);
        }
        
        // 2. 获取用户偏好的新闻类别
        List<Map<String, Object>> categoryPreferences = userNewsViewMapper.getUserCategoryPreferences(userId);
        log.info("用户类别偏好数量: {}", categoryPreferences != null ? categoryPreferences.size() : 0);
        
        // 如果没有类别偏好，返回热门新闻
        if (categoryPreferences == null || categoryPreferences.isEmpty()) {
            log.info("未找到用户类别偏好，返回热门新闻");
            return getHotNews(limit);
        }
        
        // 输出用户偏好类别
        for (Map<String, Object> category : categoryPreferences) {
            log.info("用户偏好类别: {}, 浏览次数: {}", category.get("category"), category.get("count"));
        }
        
        // 3. 获取已浏览的新闻ID列表，避免重复推荐
        List<Long> viewedNewsIds = userViews.stream()
                .map(UserNewsView::getNewsId)
                .collect(Collectors.toList());
        
        // 4. 根据用户偏好的类别推荐新闻
        List<NewsRecommendDTO> recommendList = new ArrayList<>();
        int remainingCount = limit;
        
        for (Map<String, Object> category : categoryPreferences) {
            if (remainingCount <= 0) break;
            
            Long categoryId = Long.valueOf(category.get("category").toString());
            
            // 查询该类别下的新闻，排除已浏览的
            LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(News::getCategory, categoryId)
                    .eq(News::getStatus, 3) // 已发布状态
                    .eq(News::getIsdelete, 0)
                    .notIn(viewedNewsIds.size() > 0, News::getId, viewedNewsIds)
                    .orderByDesc(News::getCreatetime);
            
            Page<News> page = new Page<>(1, remainingCount);
            Page<News> newsPage = newsMapper.selectPage(page, queryWrapper);
            
            List<NewsRecommendDTO> categoryNews = convertToDTO(newsPage.getRecords());
            recommendList.addAll(categoryNews);
            remainingCount -= categoryNews.size();
        }
        
        // 5. 如果推荐数量不足，补充热门新闻
        if (remainingCount > 0) {
            List<NewsRecommendDTO> hotNews = getHotNews(remainingCount);
            // 过滤掉已经推荐的新闻
            List<Long> recommendedIds = recommendList.stream()
                    .map(NewsRecommendDTO::getId)
                    .collect(Collectors.toList());
            
            List<NewsRecommendDTO> filteredHotNews = hotNews.stream()
                    .filter(news -> !recommendedIds.contains(news.getId()) && !viewedNewsIds.contains(news.getId()))
                    .collect(Collectors.toList());
            
            if (!filteredHotNews.isEmpty()) {
                recommendList.addAll(filteredHotNews.subList(0, Math.min(remainingCount, filteredHotNews.size())));
            }
        }
        
        return recommendList;
    }

    @Override
    public List<NewsRecommendDTO> getHotNews(Integer limit) {
        log.info("开始查询热门新闻，限制数量: {}", limit);
        
        // 查询浏览量最高的已发布新闻
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3) // 确保只查询已发布状态
                .eq(News::getIsdelete, 0)
                .orderByDesc(News::getViewcount, News::getCreatetime);
        
        List<News> newsList = newsMapper.selectList(queryWrapper);
        
        // 如果查询结果为空，尝试更宽松的查询
        if (newsList == null || newsList.isEmpty()) {
            log.warn("未找到任何已发布新闻，尝试更宽松的查询");
            queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(News::getStatus, 3) // 仍然保持已发布状态
                    .eq(News::getIsdelete, 0)
                    .orderByDesc(News::getId);
            newsList = newsMapper.selectList(queryWrapper);
        }
        
        // 记录查询到的新闻
        if (newsList != null && !newsList.isEmpty()) {
            log.info("查询到 {} 条热门新闻", newsList.size());
            for (int i = 0; i < Math.min(5, newsList.size()); i++) {
                News news = newsList.get(i);
                log.info("热门新闻 #{}: ID={}, 标题={}, 浏览量={}", i+1, news.getId(), news.getTitle(), news.getViewcount());
            }
        } else {
            log.warn("未找到任何新闻");
        }
        
        // 限制返回数量
        if (newsList != null && newsList.size() > limit) {
            newsList = newsList.subList(0, limit);
        }
        
        return convertToDTO(newsList);
    }
    
    private List<NewsRecommendDTO> convertToDTO(List<News> newsList) {
        if (newsList == null || newsList.isEmpty()) {
            return new ArrayList<>();
        }
        
        return newsList.stream().map(news -> {
            NewsRecommendDTO dto = new NewsRecommendDTO();
            dto.setId(news.getId());
            dto.setTitle(news.getTitle());
            dto.setCoverImage(news.getCoverimage());
            dto.setCategory(news.getCategory());
            
            // 获取分类名称
            NewsTag newsTag = newsTagMapper.selectById(news.getCategory());
            if (newsTag != null) {
                dto.setCategoryName(newsTag.getTagname());
            }
            
            dto.setViewCount(news.getViewcount());
            dto.setCreateTime(news.getCreatetime());
            return dto;
        }).collect(Collectors.toList());
    }
} 