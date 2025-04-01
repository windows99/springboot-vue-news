package com.guanzhi.springbootinit.service;

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
} 