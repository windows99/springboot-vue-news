package com.guanzhi.springbootinit.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.NewsPushMapper;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.UserMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsPushDTO;
import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.vo.NewsPushVO;
import com.guanzhi.springbootinit.model.vo.WebSocketMessage;
import com.guanzhi.springbootinit.service.NewsPushService;
import com.guanzhi.springbootinit.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新闻推送服务实现类
 */
@Service
@Slf4j
public class NewsPushServiceImpl extends ServiceImpl<NewsPushMapper, NewsPush> implements NewsPushService {
    
    @Resource
    private NewsPushMapper newsPushMapper;
    
    @Resource
    private NewsMapper newsMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private WebSocketServer webSocketServer;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushNewsToAllUsers() {
        // 获取最新的5条新闻
        LambdaQueryWrapper<News> newsQueryWrapper = new LambdaQueryWrapper<>();
        newsQueryWrapper.eq(News::getIsdelete, 0)
                .orderByDesc(News::getCreatetime)
                .last("LIMIT 5");
        List<News> newsList = newsMapper.selectList(newsQueryWrapper);
        
        if (newsList.isEmpty()) {
            log.warn("没有可推送的新闻");
            return 0;
        }
        
        // 获取所有活跃用户
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getIsDelete, 0);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        
        if (userList.isEmpty()) {
            log.warn("没有可推送的用户");
            return 0;
        }
        
        int count = 0;
        
        // 为每个用户推送新闻
        for (User user : userList) {
            for (News news : newsList) {
                // 检查是否已经推送过
                if (!hasPushed(user.getId(), news.getId())) {
                    NewsPush newsPush = new NewsPush();
                    newsPush.setUserId(user.getId());
                    newsPush.setNewsId(news.getId());
                    newsPush.setPushTime(new Date());
                    newsPush.setPushType(1); // 1-即时推送
                    newsPush.setStatus(1); // 1-已推送
                    newsPush.setIsRead(0); // 0-未读
                    newsPush.setCreateTime(new Date());
                    newsPush.setUpdateTime(new Date());
                    newsPush.setIsDelete(0);
                    
                    newsPushMapper.insert(newsPush);
                    
                    // 通过WebSocket发送通知
                    sendNotification(user.getId(), news);
                    
                    count++;
                }
            }
        }
        
        return count;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pushNewsToUser(Long userId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDelete() == 1) {
            log.warn("用户不存在或已删除: {}", userId);
            return false;
        }
        
        // 获取最新的5条新闻
        LambdaQueryWrapper<News> newsQueryWrapper = new LambdaQueryWrapper<>();
        newsQueryWrapper.eq(News::getIsdelete, 0)
                .orderByDesc(News::getCreatetime)
                .last("LIMIT 5");
        List<News> newsList = newsMapper.selectList(newsQueryWrapper);
        
        if (newsList.isEmpty()) {
            log.warn("没有可推送的新闻");
            return false;
        }
        
        int count = 0;
        
        // 为用户推送新闻
        for (News news : newsList) {
            // 检查是否已经推送过
            if (!hasPushed(userId, news.getId())) {
                NewsPush newsPush = new NewsPush();
                newsPush.setUserId(userId);
                newsPush.setNewsId(news.getId());
                newsPush.setPushTime(new Date());
                newsPush.setPushType(1); // 1-即时推送
                newsPush.setStatus(1); // 1-已推送
                newsPush.setIsRead(0); // 0-未读
                newsPush.setCreateTime(new Date());
                newsPush.setUpdateTime(new Date());
                newsPush.setIsDelete(0);
                
                newsPushMapper.insert(newsPush);
                
                // 通过WebSocket发送通知
                sendNotification(userId, news);
                
                count++;
            }
        }
        
        return count > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushSpecificNewsToAllUsers(Long newsId) {
        // 验证新闻是否存在
        News news = newsMapper.selectById(newsId);
        if (news == null || news.getIsdelete() == 1) {
            log.warn("新闻不存在或已删除: {}", newsId);
            return 0;
        }
        
        // 获取所有活跃用户
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getIsDelete, 0);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        
        if (userList.isEmpty()) {
            log.warn("没有可推送的用户");
            return 0;
        }
        
        int count = 0;
        
        // 为每个用户推送指定新闻
        for (User user : userList) {
            // 检查是否已经推送过
            if (!hasPushed(user.getId(), newsId)) {
                NewsPush newsPush = new NewsPush();
                newsPush.setUserId(user.getId());
                newsPush.setNewsId(newsId);
                newsPush.setPushTime(new Date());
                newsPush.setPushType(1); // 1-即时推送
                newsPush.setStatus(1); // 1-已推送
                newsPush.setIsRead(0); // 0-未读
                newsPush.setCreateTime(new Date());
                newsPush.setUpdateTime(new Date());
                newsPush.setIsDelete(0);
                
                newsPushMapper.insert(newsPush);
                
                // 通过WebSocket发送通知
                sendNotification(user.getId(), news);
                
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean pushSpecificNewsToUser(Long userId, Long newsId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDelete() == 1) {
            log.warn("用户不存在或已删除: {}", userId);
            return false;
        }
        
        // 验证新闻是否存在
        News news = newsMapper.selectById(newsId);
        if (news == null || news.getIsdelete() == 1) {
            log.warn("新闻不存在或已删除: {}", newsId);
            return false;
        }
        
        // 检查是否已经推送过
        if (hasPushed(userId, newsId)) {
            log.info("新闻已经推送给用户: userId={}, newsId={}", userId, newsId);
            return true; // 已经推送过，视为成功
        }
        
        // 创建新的推送记录
        NewsPush newsPush = new NewsPush();
        newsPush.setUserId(userId);
        newsPush.setNewsId(newsId);
        newsPush.setPushTime(new Date());
        newsPush.setPushType(1); // 1-即时推送
        newsPush.setStatus(1); // 1-已推送
        newsPush.setIsRead(0); // 0-未读
        newsPush.setCreateTime(new Date());
        newsPush.setUpdateTime(new Date());
        newsPush.setIsDelete(0);
        
        newsPushMapper.insert(newsPush);
        
        // 通过WebSocket发送通知
        sendNotification(userId, news);
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushMultipleNewsToUser(Long userId, List<Long> newsIds) {
        int successCount = 0;
        for (Long newsId : newsIds) {
            try {
                boolean success = pushSpecificNewsToUser(userId, newsId);
                if (success) {
                    successCount++;
                }
            } catch (Exception e) {
                log.error("向用户 {} 推送新闻 {} 失败: {}", userId, newsId, e.getMessage());
            }
        }
        return successCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int pushMultipleNewsToAllUsers(List<Long> newsIds) {
        int totalSuccessCount = 0;
        for (Long newsId : newsIds) {
            try {
                int count = pushSpecificNewsToAllUsers(newsId);
                totalSuccessCount += count;
            } catch (Exception e) {
                log.error("批量推送新闻 {} 失败: {}", newsId, e.getMessage());
            }
        }
        return totalSuccessCount;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean schedulePushToUser(Long userId, Date pushTime) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDelete() == 1) {
            log.warn("用户不存在或已删除: {}", userId);
            return false;
        }
        
        // 验证推送时间
        if (pushTime == null || pushTime.before(new Date())) {
            log.warn("推送时间无效: {}", pushTime);
            return false;
        }
        
        // 获取最新的5条新闻
        LambdaQueryWrapper<News> newsQueryWrapper = new LambdaQueryWrapper<>();
        newsQueryWrapper.eq(News::getIsdelete, 0)
                .orderByDesc(News::getCreatetime)
                .last("LIMIT 5");
        List<News> newsList = newsMapper.selectList(newsQueryWrapper);
        
        if (newsList.isEmpty()) {
            log.warn("没有可推送的新闻");
            return false;
        }
        
        // 创建定时推送记录
        for (News news : newsList) {
            // 跳过已经推送过的新闻
            if (hasPushed(userId, news.getId())) {
                continue;
            }
            
            NewsPush newsPush = new NewsPush();
            newsPush.setUserId(userId);
            newsPush.setNewsId(news.getId());
            newsPush.setPushTime(pushTime);
            newsPush.setPushType(2); // 2-定时推送
            newsPush.setStatus(0); // 0-待推送
            newsPush.setIsRead(0); // 0-未读
            newsPush.setCreateTime(new Date());
            newsPush.setUpdateTime(new Date());
            newsPush.setIsDelete(0);
            
            newsPushMapper.insert(newsPush);
            
            log.info("为用户 {} 设置定时推送新闻: {}, 推送时间: {}", userId, news.getTitle(), pushTime);
        }
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int schedulePushToAllUsers(Date pushTime) {
        // 验证推送时间
        if (pushTime == null || pushTime.before(new Date())) {
            log.warn("推送时间无效: {}", pushTime);
            return 0;
        }
        
        // 获取所有活跃用户
        LambdaQueryWrapper<User> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.eq(User::getIsDelete, 0);
        List<User> userList = userMapper.selectList(userQueryWrapper);
        
        if (userList.isEmpty()) {
            log.warn("没有可推送的用户");
            return 0;
        }
        
        int successCount = 0;
        
        // 为每个用户设置定时推送
        for (User user : userList) {
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
    public int executePendingPushes() {
        // 查找所有未发送的推送
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getStatus, 0) // 0-待推送
                .eq(NewsPush::getIsDelete, 0)
                .le(NewsPush::getPushTime, new Date()) // 推送时间已到
                .orderByAsc(NewsPush::getPushTime);
        
        List<NewsPush> pendingPushes = newsPushMapper.selectList(queryWrapper);
        
        if (pendingPushes.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        
        for (NewsPush push : pendingPushes) {
            try {
                // 验证用户和新闻是否存在
                User user = userMapper.selectById(push.getUserId());
                News news = newsMapper.selectById(push.getNewsId());
                
                if (user == null || user.getIsDelete() == 1) {
                    log.warn("用户不存在或已删除，忽略推送: {}", push.getUserId());
                    // 标记为推送失败，避免重复处理
                    push.setStatus(2); // 2-推送失败
                    push.setUpdateTime(new Date());
                    newsPushMapper.updateById(push);
                    continue;
                }
                
                if (news == null || news.getIsdelete() == 1) {
                    log.warn("新闻不存在或已删除，忽略推送: {}", push.getNewsId());
                    // 标记为推送失败，避免重复处理
                    push.setStatus(2); // 2-推送失败
                    push.setUpdateTime(new Date());
                    newsPushMapper.updateById(push);
                    continue;
                }
                
                // 通过WebSocket发送通知
                sendNotification(push.getUserId(), news);
                
                // 更新推送状态
                push.setStatus(1); // 1-已推送
                push.setUpdateTime(new Date());
                newsPushMapper.updateById(push);
                
                count++;
            } catch (Exception e) {
                log.error("执行推送失败: {}", e.getMessage());
                
                // 标记为推送失败
                push.setStatus(2); // 2-推送失败
                push.setUpdateTime(new Date());
                newsPushMapper.updateById(push);
            }
        }
        
        return count;
    }

    @Override
    public List<NewsPushVO> getUserPushHistory(Long userId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或已删除");
        }
        
        // 查询用户的推送历史
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getUserId, userId)
                .eq(NewsPush::getIsDelete, 0)
                .orderByDesc(NewsPush::getPushTime);
        
        List<NewsPush> pushList = newsPushMapper.selectList(queryWrapper);
        
        if (pushList.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为VO对象
        return pushList.stream().map(push -> {
            NewsPushVO vo = new NewsPushVO();
            BeanUtils.copyProperties(push, vo);
            
            // 获取新闻信息
            News news = newsMapper.selectById(push.getNewsId());
            if (news != null) {
                vo.setNewsTitle(news.getTitle());
                vo.setNewsContent(news.getContent());
                vo.setNewsCreateTime(news.getCreatetime());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<NewsPushDTO> getUserPushHistory(Long userId, int limit) {
        try {
            // 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null || user.getIsDelete() == 1) {
                log.warn("用户不存在或已删除: {}", userId);
                return new ArrayList<>();
            }
            
            // 查询用户的推送历史
            LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(NewsPush::getUserId, userId)
                    .eq(NewsPush::getIsDelete, 0)
                    .orderByDesc(NewsPush::getPushTime)
                    .last("LIMIT " + limit);
            
            List<NewsPush> pushList = newsPushMapper.selectList(queryWrapper);
            
            // 转换为DTO对象
            return pushList.stream().map(push -> {
                NewsPushDTO dto = new NewsPushDTO();
                BeanUtils.copyProperties(push, dto);
                
                // 获取新闻信息
                News news = newsMapper.selectById(push.getNewsId());
                if (news != null) {
                    dto.setNewsTitle(news.getTitle());
                    dto.setNewsCoverImage(news.getCoverimage());
                }
                
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户推送历史失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean markPushAsRead(Long pushId, Long userId) {
        NewsPush push = newsPushMapper.selectById(pushId);
        
        if (push == null || push.getIsDelete() == 1) {
            log.warn("推送记录不存在: {}", pushId);
            return false;
        }
        
        // 验证推送记录是否属于当前用户
        if (!push.getUserId().equals(userId)) {
            log.warn("推送记录不属于用户: pushId={}, userId={}", pushId, userId);
            return false;
        }
        
        // 更新状态为已读
        push.setIsRead(1); // 1-已读
        push.setReadTime(new Date());
        push.setUpdateTime(new Date());
        newsPushMapper.updateById(push);
        
        return true;
    }

    @Override
    public boolean markPushAsRead(Long pushId) {
        try {
            NewsPush push = newsPushMapper.selectById(pushId);
            if (push == null || push.getIsDelete() == 1) {
                log.warn("推送记录不存在: {}", pushId);
                return false;
            }
            
            push.setIsRead(1); // 1-已读
            push.setReadTime(new Date());
            push.setUpdateTime(new Date());
            newsPushMapper.updateById(push);
            return true;
        } catch (Exception e) {
            log.error("标记推送为已读失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletePush(Long pushId, Long userId) {
        NewsPush push = newsPushMapper.selectById(pushId);
        
        if (push == null || push.getIsDelete() == 1) {
            log.warn("推送记录不存在: {}", pushId);
            return false;
        }
        
        // 验证推送记录是否属于当前用户
        if (!push.getUserId().equals(userId)) {
            log.warn("推送记录不属于用户: pushId={}, userId={}", pushId, userId);
            return false;
        }
        
        // 逻辑删除
        push.setIsDelete(1);
        push.setUpdateTime(new Date());
        newsPushMapper.updateById(push);
        
        return true;
    }
    
    @Override
    public int getUnreadCount(Long userId) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null || user.getIsDelete() == 1) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或已删除");
        }
        
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getUserId, userId)
                .eq(NewsPush::getIsRead, 0) // 0-未读
                .eq(NewsPush::getIsDelete, 0);
        
        return newsPushMapper.selectCount(queryWrapper).intValue();
    }

    @Override
    public List<Long> getRecentlyPushedNewsIds(Long userId, int limit) {
        try {
            // 验证用户是否存在
            User user = userMapper.selectById(userId);
            if (user == null || user.getIsDelete() == 1) {
                log.warn("用户不存在或已删除: {}", userId);
                return new ArrayList<>();
            }
            
            // 查询用户最近的推送记录
            LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(NewsPush::getUserId, userId)
                    .eq(NewsPush::getIsDelete, 0)
                    .orderByDesc(NewsPush::getPushTime)
                    .last("LIMIT " + limit);
            
            List<NewsPush> pushList = newsPushMapper.selectList(queryWrapper);
            
            // 提取新闻ID
            return pushList.stream()
                    .map(NewsPush::getNewsId)
                    .distinct()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取用户最近推送新闻列表失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * 检查是否已经推送过该新闻给用户
     */
    private boolean hasPushed(Long userId, Long newsId) {
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getUserId, userId)
                .eq(NewsPush::getNewsId, newsId)
                .eq(NewsPush::getIsDelete, 0);
        
        return newsPushMapper.selectCount(queryWrapper) > 0;
    }
    
    /**
     * 通过WebSocket发送通知
     */
    private void sendNotification(Long userId, News news) {
        try {
            NewsPushDTO pushDTO = new NewsPushDTO();
            pushDTO.setUserId(userId);
            pushDTO.setNewsId(news.getId());
            pushDTO.setNewsTitle(news.getTitle());
            
            // 使用WebSocketServer直接发送消息
            WebSocketServer.sendMessageToUser(userId.toString(), "您有一条新消息：" + news.getTitle());
            
            // 也可以使用WebSocketMessage包装消息
            // WebSocketMessage<NewsPushDTO> message = WebSocketMessage.build(2, "您有一条新消息", pushDTO);
            // 使用JSON工具转换为字符串后发送
            // WebSocketServer.sendMessageToUser(userId.toString(), JSON.toJSONString(message));
        } catch (Exception e) {
            log.error("发送WebSocket通知失败: {}", e.getMessage());
        }
    }
} 