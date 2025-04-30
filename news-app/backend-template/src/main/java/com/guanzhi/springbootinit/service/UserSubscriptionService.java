package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.UserSubscription;

import java.util.List;

/**
 * 用户订阅服务
 */
public interface UserSubscriptionService extends IService<UserSubscription> {

    /**
     * 保存用户订阅的分类
     *
     * @param userId 用户ID
     * @param categories 分类列表
     * @return 是否保存成功
     */
    boolean saveSubscriptions(Long userId, List<String> categories);
} 