package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.UserSubscription;

import java.util.List;

/**
 * 用户订阅服务
 *
 * @author sk
 */
public interface UserSubscriptionService extends IService<UserSubscription> {

    /**
     * 添加用户订阅
     *
     * @param userId 用户ID
     * @param category 订阅分类
     * @return 是否成功
     */
    boolean addSubscription(Long userId, String category);

    /**
     * 取消用户订阅
     *
     * @param userId 用户ID
     * @param category 订阅分类
     * @return 是否成功
     */
    boolean cancelSubscription(Long userId, String category);

    /**
     * 获取用户的所有订阅分类
     *
     * @param userId 用户ID
     * @return 订阅分类列表
     */
    List<String> getUserSubscriptions(Long userId);

    /**
     * 检查用户是否订阅了某个分类
     *
     * @param userId 用户ID
     * @param category 订阅分类
     * @return 是否订阅
     */
    boolean isSubscribed(Long userId, String category);
} 