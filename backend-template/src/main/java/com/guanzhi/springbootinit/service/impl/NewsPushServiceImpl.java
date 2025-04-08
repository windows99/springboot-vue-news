package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.NewsPushMapper;
import com.guanzhi.springbootinit.mapper.UserMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.vo.WebSocketMessage;
import com.guanzhi.springbootinit.service.NewsRecommendService;
import com.guanzhi.springbootinit.service.NewsPushService;
import com.guanzhi.springbootinit.service.NewsWebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsPushServiceImpl implements NewsPushService {

    @Resource
    private NewsRecommendService newsRecommendService;
    
    @Resource
    private NewsPushMapper newsPushMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private NewsMapper newsMapper;
    
    // 每次推送的新闻数量
    private static final int PUSH_NEWS_COUNT = 5;
    
    // 用户最近推送的新闻数量限制
    private static final int RECENT_PUSH_LIMIT = 20;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pushNewsToUser(Long userId) {
        // 获取用户最近推送的新闻ID列表
        List<Long> recentlyPushedNewsIds = getRecentlyPushedNewsIds(userId, RECENT_PUSH_LIMIT);
        
        // 获取用户个性化推荐的新闻
        List<NewsRecommendDTO> recommendNews = newsRecommendService.getPersonalizedNews(userId, PUSH_NEWS_COUNT * 2);
        
        if (recommendNews == null || recommendNews.isEmpty()) {
            log.warn("没有找到适合用户 {} 的推荐新闻", userId);
            return false;
        }
        
        // 过滤掉最近推送过的新闻
        List<NewsRecommendDTO> filteredNews = recommendNews.stream()
                .filter(news -> !recentlyPushedNewsIds.contains(news.getId()))
                .collect(Collectors.toList());
        
        // 如果过滤后的新闻不足，则从原始推荐中补充
        if (filteredNews.size() < PUSH_NEWS_COUNT) {
            int needMore = PUSH_NEWS_COUNT - filteredNews.size();
            List<NewsRecommendDTO> additionalNews = recommendNews.stream()
                    .filter(news -> recentlyPushedNewsIds.contains(news.getId()))
                    .limit(needMore)
                    .collect(Collectors.toList());
            filteredNews.addAll(additionalNews);
        }
        
        // 限制推送数量
        if (filteredNews.size() > PUSH_NEWS_COUNT) {
            filteredNews = filteredNews.subList(0, PUSH_NEWS_COUNT);
        }
        
        // 记录推送
        Date now = new Date();
        for (NewsRecommendDTO news : filteredNews) {
            NewsPush newsPush = new NewsPush();
            newsPush.setUserId(userId);
            newsPush.setNewsId(news.getId());
            newsPush.setPushTime(now);
            newsPush.setPushType(1); // 1-即时推送
            newsPush.setStatus(1); // 1-已推送
            newsPush.setCreateTime(now);
            newsPush.setUpdateTime(now);
            newsPush.setIsDelete(0);
            
            newsPushMapper.insert(newsPush);
            
            log.info("向用户 {} 推送新闻: {}", userId, news.getTitle());
        }
        
        // 通过WebSocket发送通知
        try {
            StringBuilder message = new StringBuilder("您有新的推荐新闻：");
            for (int i = 0; i < Math.min(3, filteredNews.size()); i++) {
                message.append("\n").append(i + 1).append(". ").append(filteredNews.get(i).getTitle());
            }
            
            WebSocketMessage wsMessage = WebSocketMessage.build(2, message.toString(), filteredNews);
            NewsWebSocketServer.sendInfo(wsMessage, userId.toString());
        } catch (Exception e) {
            log.error("WebSocket推送失败: {}", e.getMessage());
        }
        
        return true;
    }

    @Override
    public int pushNewsToAllUsers() {
        // 获取所有活跃用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsDelete, 0)
                .ne(User::getUserRole, "ban"); // 排除被禁用的用户
        
        List<User> users = userMapper.selectList(queryWrapper);
        
        int successCount = 0;
        for (User user : users) {
            try {
                boolean success = pushNewsToUser(user.getId());
                if (success) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("向用户 {} 推送新闻失败: {}", user.getId(), e.getMessage());
            }
        }
        
        log.info("成功向 {} 个用户推送了新闻", successCount);
        return successCount;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean schedulePushToUser(Long userId, Date pushTime) {
        // 获取用户最近推送的新闻ID列表
        List<Long> recentlyPushedNewsIds = getRecentlyPushedNewsIds(userId, RECENT_PUSH_LIMIT);
        
        // 获取用户个性化推荐的新闻
        List<NewsRecommendDTO> recommendNews = newsRecommendService.getPersonalizedNews(userId, PUSH_NEWS_COUNT * 2);
        
        if (recommendNews == null || recommendNews.isEmpty()) {
            log.warn("没有找到适合用户 {} 的推荐新闻", userId);
            return false;
        }
        
        // 过滤掉最近推送过的新闻
        List<NewsRecommendDTO> filteredNews = recommendNews.stream()
                .filter(news -> !recentlyPushedNewsIds.contains(news.getId()))
                .collect(Collectors.toList());
        
        // 如果过滤后的新闻不足，则从原始推荐中补充
        if (filteredNews.size() < PUSH_NEWS_COUNT) {
            int needMore = PUSH_NEWS_COUNT - filteredNews.size();
            List<NewsRecommendDTO> additionalNews = recommendNews.stream()
                    .filter(news -> recentlyPushedNewsIds.contains(news.getId()))
                    .limit(needMore)
                    .collect(Collectors.toList());
            filteredNews.addAll(additionalNews);
        }
        
        // 限制推送数量
        if (filteredNews.size() > PUSH_NEWS_COUNT) {
            filteredNews = filteredNews.subList(0, PUSH_NEWS_COUNT);
        }
        
        // 记录定时推送
        Date now = new Date();
        for (NewsRecommendDTO news : filteredNews) {
            NewsPush newsPush = new NewsPush();
            newsPush.setUserId(userId);
            newsPush.setNewsId(news.getId());
            newsPush.setPushTime(pushTime);
            newsPush.setPushType(2); // 2-定时推送
            newsPush.setStatus(0); // 0-待推送
            newsPush.setCreateTime(now);
            newsPush.setUpdateTime(now);
            newsPush.setIsDelete(0);
            
            newsPushMapper.insert(newsPush);
            
            log.info("为用户 {} 设置定时推送新闻: {}, 推送时间: {}", userId, news.getTitle(), pushTime);
        }
        
        return true;
    }
    
    @Override
    public int schedulePushToAllUsers(Date pushTime) {
        // 获取所有活跃用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsDelete, 0)
                .ne(User::getUserRole, "ban"); // 排除被禁用的用户
        
        List<User> users = userMapper.selectList(queryWrapper);
        
        int successCount = 0;
        for (User user : users) {
            try {
                boolean success = schedulePushToUser(user.getId(), pushTime);
                if (success) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("为用户 {} 设置定时推送失败: {}", user.getId(), e.getMessage());
            }
        }
        
        log.info("成功为 {} 个用户设置了定时推送", successCount);
        return successCount;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushSpecificNewsToAllUsers(Long newsId) {
        // 检查新闻是否存在
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            log.warn("新闻 {} 不存在", newsId);
            return 0;
        }
        
        // 获取所有活跃用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsDelete, 0)
                .ne(User::getUserRole, "ban"); // 排除被禁用的用户
        
        List<User> users = userMapper.selectList(queryWrapper);
        
        int successCount = 0;
        for (User user : users) {
            try {
                boolean success = pushSpecificNewsToUser(user.getId(), newsId);
                if (success) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("向用户 {} 推送指定新闻 {} 失败: {}", user.getId(), newsId, e.getMessage());
            }
        }
        
        log.info("成功向 {} 个用户推送了指定新闻 {}", successCount, newsId);
        return successCount;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pushSpecificNewsToUser(Long userId, Long newsId) {
        // 检查新闻是否存在
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            log.warn("新闻 {} 不存在", newsId);
            return false;
        }
        
        // 检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDelete() == 1 || "ban".equals(user.getUserRole())) {
            log.warn("用户 {} 不存在或已被禁用", userId);
            return false;
        }
        
        // 记录推送
        Date now = new Date();
        NewsPush newsPush = new NewsPush();
        newsPush.setUserId(userId);
        newsPush.setNewsId(newsId);
        newsPush.setPushTime(now);
        newsPush.setPushType(1); // 1-即时推送
        newsPush.setStatus(1); // 1-已推送
        newsPush.setCreateTime(now);
        newsPush.setUpdateTime(now);
        newsPush.setIsDelete(0);
        
        newsPushMapper.insert(newsPush);
        
        // 通过WebSocket发送通知
        try {
            String message = "您有一条新的新闻推送：" + news.getTitle();
            
            NewsRecommendDTO newsDTO = new NewsRecommendDTO();
            newsDTO.setId(news.getId());
            newsDTO.setTitle(news.getTitle());
            newsDTO.setContent(news.getContent());
            newsDTO.setCoverimage(news.getCoverimage());
            newsDTO.setCategory(news.getCategory());
            newsDTO.setCategoryName("分类" + news.getCategory());
            newsDTO.setViewcount(news.getViewcount());
            newsDTO.setCreatetime(news.getCreatetime());
            
            List<NewsRecommendDTO> newsList = Collections.singletonList(newsDTO);
            WebSocketMessage wsMessage = WebSocketMessage.build(2, message, newsList);
            NewsWebSocketServer.sendInfo(wsMessage, userId.toString());
            
            log.info("向用户 {} 推送指定新闻: {}", userId, news.getTitle());
        } catch (Exception e) {
            log.error("WebSocket推送失败: {}", e.getMessage());
        }
        
        return true;
    }
    
    @Override
    public List<Long> getRecentlyPushedNewsIds(Long userId, int limit) {
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getIsDelete, 0)
                .orderByDesc(NewsPush::getPushTime)
                .last("LIMIT " + limit);
        
        List<NewsPush> pushes = newsPushMapper.selectList(queryWrapper);
        return pushes.stream()
                .map(NewsPush::getNewsId)
                .collect(Collectors.toList());
    }
} 