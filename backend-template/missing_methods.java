// 缺失的方法，需要添加到NewsPushServiceImpl类中

/**
 * 标记推送为已读
 */
@Override
public boolean markPushAsRead(Long pushId) {
    try {
        NewsPush newsPush = newsPushMapper.selectById(pushId);
        if (newsPush == null) {
            log.warn("推送记录不存在: {}", pushId);
            return false;
        }
        
        newsPush.setIsRead(1);
        newsPush.setReadTime(new Date());
        newsPush.setUpdateTime(new Date());
        
        newsPushMapper.updateById(newsPush);
        return true;
    } catch (Exception e) {
        log.error("标记推送为已读失败: {}", e.getMessage());
        return false;
    }
}

/**
 * 批量推送多个新闻给指定用户
 */
@Override
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

/**
 * 批量推送多个新闻给所有用户
 */
@Override
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

/**
 * 获取用户的推送历史
 */
@Override
public List<com.guanzhi.springbootinit.model.dto.news.NewsPushDTO> getUserPushHistory(Long userId, int limit) {
    try {
        return newsPushMapper.getUserPushHistory(userId, limit);
    } catch (Exception e) {
        log.error("获取用户推送历史失败: {}", e.getMessage());
        return new ArrayList<>();
    }
}

/**
 * 执行待推送任务
 */
@Override
@Transactional(rollbackFor = Exception.class)
public int executePendingPushes() {
    try {
        // 获取所有待推送的记录
        List<NewsPush> pendingPushes = newsPushMapper.getPendingPushes();
        if (pendingPushes.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        Date now = new Date();
        
        for (NewsPush push : pendingPushes) {
            try {
                // 查询新闻是否存在
                News news = newsMapper.selectById(push.getNewsId());
                if (news == null) {
                    // 新闻不存在，标记为推送失败
                    push.setStatus(2); // 2-推送失败
                    push.setUpdateTime(now);
                    newsPushMapper.updateById(push);
                    continue;
                }
                
                // 查询用户是否存在
                User user = userMapper.selectById(push.getUserId());
                if (user == null) {
                    // 用户不存在，标记为推送失败
                    push.setStatus(2); // 2-推送失败
                    push.setUpdateTime(now);
                    newsPushMapper.updateById(push);
                    continue;
                }
                
                // 执行推送
                try {
                    // 准备新闻信息
                    List<NewsRecommendDTO> newsList = new ArrayList<>();
                    NewsRecommendDTO newsDto = new NewsRecommendDTO();
                    newsDto.setId(news.getId());
                    newsDto.setTitle(news.getTitle());
                    newsDto.setCoverImage(news.getCoverimage());
                    newsDto.setContent(news.getContent());
                    newsList.add(newsDto);
                    
                    // 通过WebSocket发送通知
                    StringBuilder message = new StringBuilder("您有新的推送新闻：\n");
                    message.append(news.getTitle());
                    
                    WebSocketMessage wsMessage = WebSocketMessage.build(2, message.toString(), newsList);
                    NewsWebSocketServer.sendInfo(wsMessage, push.getUserId().toString());
                    
                    // 标记为已推送
                    push.setStatus(1); // 1-已推送
                    push.setUpdateTime(now);
                    newsPushMapper.updateById(push);
                    
                    successCount++;
                    log.info("成功推送新闻 {} 给用户 {}", push.getNewsId(), push.getUserId());
                } catch (Exception e) {
                    log.error("推送新闻失败: {}", e.getMessage());
                    // 标记为推送失败
                    push.setStatus(2); // 2-推送失败
                    push.setUpdateTime(now);
                    newsPushMapper.updateById(push);
                }
            } catch (Exception e) {
                log.error("处理待推送记录 {} 失败: {}", push.getId(), e.getMessage());
            }
        }
        
        log.info("成功执行了 {} 个待推送任务", successCount);
        return successCount;
    } catch (Exception e) {
        log.error("执行待推送任务失败: {}", e.getMessage());
        return 0;
    }
} 