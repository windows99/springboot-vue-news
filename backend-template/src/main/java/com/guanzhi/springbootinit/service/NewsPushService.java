package com.guanzhi.springbootinit.service;

import java.util.Date;

public interface NewsPushService {
    /**
     * 为指定用户推送新闻
     * @param userId 用户ID
     * @return 是否推送成功
     */
    boolean pushNewsToUser(Long userId);
    
    /**
     * 为所有用户推送个性化新闻
     * @return 推送成功的用户数
     */
    int pushNewsToAllUsers();
    
    /**
     * 为指定用户推送定时新闻
     * @param userId 用户ID
     * @param pushTime 推送时间
     * @return 是否设置成功
     */
    boolean schedulePushToUser(Long userId, Date pushTime);
    
    /**
     * 为所有用户推送定时新闻
     * @param pushTime 推送时间
     * @return 设置成功的用户数
     */
    int schedulePushToAllUsers(Date pushTime);
    
    /**
     * 手动推送指定新闻给所有用户
     * @param newsId 新闻ID
     * @return 推送成功的用户数
     */
    int pushSpecificNewsToAllUsers(Long newsId);
    
    /**
     * 手动推送指定新闻给特定用户
     * @param userId 用户ID
     * @param newsId 新闻ID
     * @return 是否推送成功
     */
    boolean pushSpecificNewsToUser(Long userId, Long newsId);
    
    /**
     * 获取用户最近推送的新闻ID列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 新闻ID列表
     */
    java.util.List<Long> getRecentlyPushedNewsIds(Long userId, int limit);
} 