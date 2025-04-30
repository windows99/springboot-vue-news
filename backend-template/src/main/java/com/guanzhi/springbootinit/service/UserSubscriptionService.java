package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.UserSubscription;

import java.util.List;
import java.util.Map;

/**
 * 用户订阅服务
 *
 * @author sk
 */
public interface UserSubscriptionService extends IService<UserSubscription> {

    /**
     * 保存用户的订阅标签
     *
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 是否保存成功
     */
    boolean saveSubscriptions(Long userId, List<Long> tagIds);

    /**
     * 获取用户的所有订阅标签
     *
     * @param userId 用户ID
     * @return 标签列表，包含ID和名称
     */
    List<Map<String, Object>> getUserSubscriptions(Long userId);

    /**
     * 检查用户是否订阅了某个标签
     *
     * @param userId 用户ID
     * @param tagId 标签ID
     * @return 是否已订阅
     */
    boolean isSubscribed(Long userId, String tagId);
} 