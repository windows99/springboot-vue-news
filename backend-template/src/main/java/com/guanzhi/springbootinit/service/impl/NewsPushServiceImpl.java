package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guanzhi.springbootinit.mapper.NewsPushMapper;
import com.guanzhi.springbootinit.mapper.UserMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.vo.WebSocketMessage;
import com.guanzhi.springbootinit.service.NewsRecommendService;
import com.guanzhi.springbootinit.service.NewsPushService;
import com.guanzhi.springbootinit.service.NewsWebSocketServer;
//import com.guanzhi.springbootinit.model.entity.WebSocketMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class NewsPushServiceImpl implements NewsPushService {

    @Resource
    private NewsRecommendService newsRecommendService;
    
    @Resource
    private NewsPushMapper newsPushMapper;
    
    @Resource
    private UserMapper userMapper;
    
    // 每次推送的新闻数量
    private static final int PUSH_NEWS_COUNT = 5;

    @Override
    public boolean pushNewsToUser(Long userId) {
        // 获取用户个性化推荐的新闻
        List<NewsRecommendDTO> recommendNews = newsRecommendService.getPersonalizedNews(userId, PUSH_NEWS_COUNT);
        
        if (recommendNews == null || recommendNews.isEmpty()) {
            log.warn("没有找到适合用户 {} 的推荐新闻", userId);
            return false;
        }
        
        // 记录推送
        Date now = new Date();
        for (NewsRecommendDTO news : recommendNews) {
            NewsPush newsPush = new NewsPush();
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
            for (int i = 0; i < Math.min(3, recommendNews.size()); i++) {
                message.append("\n").append(i + 1).append(". ").append(recommendNews.get(i).getTitle());
            }
            
            WebSocketMessage wsMessage = WebSocketMessage.build(2, message.toString(), recommendNews);
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
} 