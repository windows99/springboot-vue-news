package com.guanzhi.springbootinit.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.NewsPushMapper;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.UserSubscriptionMapper;
import com.guanzhi.springbootinit.model.dto.NewsPushDTO;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import com.guanzhi.springbootinit.model.vo.WebSocketMessage;
import com.guanzhi.springbootinit.model.vo.NewsPushVO;
import com.guanzhi.springbootinit.service.NewsPushService;
import com.guanzhi.springbootinit.service.NewsService;
import com.guanzhi.springbootinit.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 新闻推送服务实现
 */
@Service
@Slf4j
public class NewsPushServiceImpl implements NewsPushService {
    
    @Resource
    private NewsPushMapper newsPushMapper;
    
    @Resource
    private NewsMapper newsMapper;
    
    @Resource
    private NewsService newsService;
    
    @Resource
    private UserSubscriptionMapper userSubscriptionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pushImmediately(Long newsId, List<Long> userIds) {
        if (newsId == null || userIds == null || userIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 获取新闻信息
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "新闻不存在");
        }
        
        // 创建推送记录列表
        List<NewsPush> pushList = new ArrayList<>();
        for (Long userId : userIds) {
            try {
                // 创建推送记录
                NewsPush newsPush = new NewsPush();
                newsPush.setUserId(userId);
                newsPush.setNewsId(newsId);
                newsPush.setPushTime(new Date());
                newsPush.setPushType(1); // 即时推送
                newsPush.setStatus(1);   // 已推送
                newsPush.setIsRead(0);   // 未读
                newsPush.setIsDelete(0); // 未删除
                pushList.add(newsPush);
            } catch (Exception e) {
                log.error("创建推送记录失败，用户ID：{}，新闻ID：{}", userId, newsId, e);
            }
        }
        
        // 批量插入推送记录
        if (!pushList.isEmpty()) {
            newsPushMapper.insertBatch(pushList);
        }
        
        // 构建推送消息
        NewsPushDTO pushDTO = new NewsPushDTO();
        pushDTO.setNewsId(String.valueOf(news.getId()));
        pushDTO.setTitle(news.getTitle());
        pushDTO.setContent(news.getContent());
        pushDTO.setCoverImage(news.getCoverImage());
        pushDTO.setSource(news.getSource());
        pushDTO.setAuthor(news.getAuthor());
        pushDTO.setPublishTime(news.getCreatetime());
        pushDTO.setViewCount(news.getViewcount());
        pushDTO.setLikeCount(news.getLikeCount());
        pushDTO.setCommentCount(news.getCommentCount());
        pushDTO.setCategory(String.valueOf(news.getCategory()));
        pushDTO.setStatus(news.getStatus());
        pushDTO.setIsDelete(news.getIsdelete());
        pushDTO.setCreateTime(news.getCreatetime());
        pushDTO.setUpdateTime(news.getUpdateTime());
        
        // 发送批量推送消息
        for (Long userId : userIds) {
            try {
                pushDTO.setUserId(String.valueOf(userId));
                WebSocketMessage<NewsPushDTO> message = WebSocketMessage.buildBatch(3, "您有新的新闻推送", List.of(pushDTO));
                String messageJson = JSON.toJSONString(message);
                WebSocketServer.sendMessageToUser(userId.toString(), messageJson);
            } catch (Exception e) {
                log.error("发送WebSocket通知失败，用户ID: {}, 新闻ID: {}", userId, newsId, e);
            }
        }
        
        return true;
    }

    @Override
    public int pushMultipleNews(List<Long> newsIds, List<Long> userIds) {
        if (newsIds == null || newsIds.isEmpty() || userIds == null || userIds.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        int successCount = 0;
        Map<Long, News> newsMap = new HashMap<>();
        List<NewsPush> pushList = new ArrayList<>();
        
        // 批量获取新闻信息
        for (Long newsId : newsIds) {
            News news = newsMapper.selectById(newsId);
            if (news != null) {
                newsMap.put(newsId, news);
            }
        }
        
        // 创建推送记录
        for (Long newsId : newsMap.keySet()) {
            News news = newsMap.get(newsId);
            for (Long userId : userIds) {
                try {
                    NewsPush newsPush = new NewsPush();
                    newsPush.setUserId(userId);
                    newsPush.setNewsId(newsId);
                    newsPush.setPushTime(new Date());
                    newsPush.setPushType(1); // 即时推送
                    newsPush.setStatus(1);   // 已推送
                    newsPush.setIsRead(0);   // 未读
                    newsPush.setIsDelete(0); // 未删除
                    pushList.add(newsPush);
                    successCount++;
                } catch (Exception e) {
                    log.error("创建推送记录失败，用户ID：{}，新闻ID：{}", userId, newsId, e);
                }
            }
        }
        
        // 批量插入推送记录
        if (!pushList.isEmpty()) {
            newsPushMapper.insertBatch(pushList);
        }
        
        // 按用户分组发送批量推送消息
        for (Long userId : userIds) {
            try {
                List<NewsPushDTO> pushDTOs = newsMap.values().stream()
                    .map(news -> {
                        NewsPushDTO dto = new NewsPushDTO();
                        dto.setUserId(String.valueOf(userId));
                        dto.setNewsId(String.valueOf(news.getId()));
                        dto.setTitle(news.getTitle());
                        dto.setContent(news.getContent());
                        dto.setCoverImage(news.getCoverImage());
                        dto.setSource(news.getSource());
                        dto.setAuthor(news.getAuthor());
                        dto.setPublishTime(news.getCreatetime());
                        dto.setViewCount(news.getViewcount());
                        dto.setLikeCount(news.getLikeCount());
                        dto.setCommentCount(news.getCommentCount());
                        dto.setCategory(String.valueOf(news.getCategory()));
                        dto.setStatus(news.getStatus());
                        dto.setIsDelete(news.getIsdelete());
                        dto.setCreateTime(news.getCreatetime());
                        dto.setUpdateTime(news.getUpdateTime());
                        return dto;
                    })
                    .collect(Collectors.toList());
                
                WebSocketMessage<NewsPushDTO> message = WebSocketMessage.buildBatch(3, "您有新的新闻推送", pushDTOs);
                String messageJson = JSON.toJSONString(message);
                WebSocketServer.sendMessageToUser(userId.toString(), messageJson);
            } catch (Exception e) {
                log.error("发送WebSocket通知失败，用户ID: {}", userId, e);
            }
        }
        
        return successCount;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushPersonalizedNews(Long userId, Integer limit) {
        // 获取用户订阅的标签
        List<Long> tagIds = userSubscriptionMapper.getUserTags(userId);
        if (tagIds.isEmpty()) {
            log.info("用户 {} 没有订阅任何标签，跳过个性化推送", userId);
            return 0;
        }
        
        // 获取用户最近浏览的新闻ID列表（用于去重）
        List<Long> recentViewedNewsIds = newsPushMapper.getRecentViewedNewsIds(userId, 20); // 减少到最近20条浏览记录
        
        // 获取用户浏览历史中的标签权重
        Map<Long, Integer> tagWeights = calculateTagWeights(userId);
        
        // 根据标签和权重获取新闻，并排除已浏览的新闻
        List<News> newsList = newsMapper.getNewsByTagsWithWeights(tagIds, tagWeights, recentViewedNewsIds, limit);
        if (newsList.isEmpty()) {
            log.info("没有找到符合用户 {} 兴趣的新闻", userId);
            return 0;
        }
        
        // 创建推送记录
        List<NewsPush> pushList = new ArrayList<>();
        for (News news : newsList) {
            NewsPush newsPush = new NewsPush();
            newsPush.setUserId(userId);
            newsPush.setNewsId(news.getId());
            newsPush.setPushTime(new Date());
            newsPush.setPushType(2); // 个性化推送
            newsPush.setStatus(1);   // 已推送
            newsPush.setIsRead(0);   // 未读
            newsPush.setIsDelete(0); // 未删除
            pushList.add(newsPush);
        }
        
        // 批量插入推送记录
        if (!pushList.isEmpty()) {
            newsPushMapper.insertBatch(pushList);
        }
        
        // 构建并发送批量推送消息
        List<NewsPushDTO> pushDTOs = newsList.stream()
            .map(news -> {
                NewsPushDTO dto = new NewsPushDTO();
                dto.setUserId(String.valueOf(userId));
                dto.setNewsId(String.valueOf(news.getId()));
                dto.setTitle(news.getTitle());
                dto.setContent(news.getContent());
                dto.setCoverImage(news.getCoverImage());
                dto.setSource(news.getSource());
                dto.setAuthor(news.getAuthor());
                dto.setPublishTime(news.getCreatetime());
                dto.setViewCount(news.getViewcount());
                dto.setLikeCount(news.getLikeCount());
                dto.setCommentCount(news.getCommentCount());
                dto.setCategory(String.valueOf(news.getCategory()));
                dto.setStatus(news.getStatus());
                dto.setIsDelete(news.getIsdelete());
                dto.setCreateTime(news.getCreatetime());
                dto.setUpdateTime(news.getUpdateTime());
                return dto;
            })
            .collect(Collectors.toList());
        
        try {
            WebSocketMessage<NewsPushDTO> message = WebSocketMessage.buildBatch(3, "为您推荐以下新闻", pushDTOs);
            String messageJson = JSON.toJSONString(message);
            WebSocketServer.sendMessageToUser(userId.toString(), messageJson);
        } catch (Exception e) {
            log.error("发送WebSocket通知失败，用户ID: {}", userId, e);
        }
        
        return newsList.size();
    }

    /**
     * 计算用户标签权重
     * 基于用户浏览历史，计算每个标签的权重
     * 权重计算规则：
     * 1. 最近浏览的新闻权重更高
     * 2. 浏览时间越近，权重越高
     * 3. 浏览次数越多，权重越高
     */
    private Map<Long, Integer> calculateTagWeights(Long userId) {
        Map<Long, Integer> tagWeights = new HashMap<>();
        
        // 获取用户最近浏览的新闻及其标签
        List<Map<String, Object>> recentViews = newsPushMapper.getRecentViewedNewsWithTags(userId, 100);
        
        // 计算每个标签的权重
        for (Map<String, Object> view : recentViews) {
            Long newsId = ((Number) view.get("newsId")).longValue();
            Long tagId = ((Number) view.get("tagId")).longValue();
            Date viewTime = (Date) view.get("readTime");
            
            // 计算时间衰减因子（最近7天的浏览记录权重更高）
            long timeDiff = System.currentTimeMillis() - viewTime.getTime();
            double timeFactor = timeDiff <= 7 * 24 * 60 * 60 * 1000 ? 1.0 : 0.5;
            
            // 更新标签权重
            int currentWeight = tagWeights.getOrDefault(tagId, 0);
            tagWeights.put(tagId, currentWeight + (int)(10 * timeFactor));
        }
        
        return tagWeights;
    }

    @Override
    public List<NewsPushDTO> getUnreadPushes(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 使用LambdaQueryWrapper获取未读推送
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getUserId, userId)
                .eq(NewsPush::getIsRead, 0)
                .eq(NewsPush::getIsDelete, 0)
                .orderByDesc(NewsPush::getPushTime);
        
        List<NewsPush> pushList = newsPushMapper.selectList(queryWrapper);
        
        // 转换为DTO
        return pushList.stream().map(push -> {
            News news = newsMapper.selectById(push.getNewsId());
            if (news == null) {
                return null;
            }
            
            NewsPushDTO dto = new NewsPushDTO();
            dto.setUserId(String.valueOf(push.getUserId()));
            dto.setNewsId(String.valueOf(push.getNewsId()));
            dto.setTitle(news.getTitle());
            dto.setContent(news.getContent());
            dto.setCoverImage(news.getCoverImage());
            dto.setSource(news.getSource());
            dto.setAuthor(news.getAuthor());
            dto.setPublishTime(news.getCreatetime());
            dto.setViewCount(news.getViewcount());
            dto.setLikeCount(news.getLikeCount());
            dto.setCommentCount(news.getCommentCount());
            dto.setCategory(String.valueOf(news.getCategory()));
            dto.setStatus(news.getStatus());
            dto.setIsDelete(news.getIsdelete());
            dto.setCreateTime(news.getCreatetime());
            dto.setUpdateTime(news.getUpdateTime());
            
            return dto;
        }).filter(dto -> dto != null).collect(Collectors.toList());
    }
    
    @Override
    public boolean markAsRead(Long pushId) {
        if (pushId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 使用LambdaUpdateWrapper标记已读
        LambdaUpdateWrapper<NewsPush> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NewsPush::getId, pushId)
                .set(NewsPush::getIsRead, 1)
                .set(NewsPush::getReadTime, new Date());
        
        return newsPushMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public Page<NewsPush> getUserPushRecords(Long userId, long current, long pageSize, Integer isRead) {
        // 查询用户的推送记录
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getUserId, userId)
                .eq(NewsPush::getIsDelete, 0)
                .eq(isRead != null, NewsPush::getIsRead, isRead)
                .orderByDesc(NewsPush::getPushTime);
        
        Page<NewsPush> page = new Page<>(current, pageSize);
        return newsPushMapper.selectPage(page, queryWrapper);
    }
    
    @Override
    public NewsPush getPushRecordDetail(Long recordId, Long userId) {
        // 查询单个推送记录
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getId, recordId)
                .eq(NewsPush::getUserId, userId)
                .eq(NewsPush::getIsDelete, 0);
        
        NewsPush pushRecord = newsPushMapper.selectOne(queryWrapper);
        if (pushRecord == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "推送记录不存在");
        }
        
        return pushRecord;
    }

    @Override
    public Page<NewsPushVO> getUserPushRecordsWithNews(Long userId, long current, long pageSize, Integer isRead) {
        // 查询用户的推送记录
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getUserId, userId)
                .eq(NewsPush::getIsDelete, 0)
                .eq(isRead != null, NewsPush::getIsRead, isRead)
                .orderByDesc(NewsPush::getPushTime);
        
        Page<NewsPush> page = new Page<>(current, pageSize);
        Page<NewsPush> pushPage = newsPushMapper.selectPage(page, queryWrapper);
        
        // 转换为包含新闻标题的VO
        List<NewsPushVO> pushVOList = new ArrayList<>();
        for (NewsPush push : pushPage.getRecords()) {
            NewsPushVO pushVO = new NewsPushVO();
            pushVO.setNewsPush(push);
            
            // 查询新闻信息
            News news = newsMapper.selectById(push.getNewsId());
            if (news != null) {
                pushVO.setNewsTitle(news.getTitle());
                pushVO.setNewsCoverImage(news.getCoverImage());
            }
            
            pushVOList.add(pushVO);
        }
        
        // 创建新的Page对象并返回
        Page<NewsPushVO> resultPage = new Page<>(current, pageSize, pushPage.getTotal());
        resultPage.setRecords(pushVOList);
        
        return resultPage;
    }
} 