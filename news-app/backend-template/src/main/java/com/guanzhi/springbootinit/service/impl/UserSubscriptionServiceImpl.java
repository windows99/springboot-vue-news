package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.UserSubscriptionMapper;
import com.guanzhi.springbootinit.model.entity.UserSubscription;
import com.guanzhi.springbootinit.service.UserSubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户订阅服务实现
 */
@Service
public class UserSubscriptionServiceImpl extends ServiceImpl<UserSubscriptionMapper, UserSubscription> implements UserSubscriptionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSubscriptions(Long userId, List<String> categories) {
        // 先删除用户的所有订阅
        lambdaUpdate().eq(UserSubscription::getUserId, userId).remove();
        
        // 如果没有分类，直接返回成功
        if (categories == null || categories.isEmpty()) {
            return true;
        }
        
        // 构建新的订阅记录
        List<UserSubscription> subscriptions = categories.stream()
            .map(category -> {
                UserSubscription subscription = new UserSubscription();
                subscription.setUserId(userId);
                subscription.setCategory(category);
                return subscription;
            })
            .collect(Collectors.toList());
        
        // 批量保存新的订阅记录
        return saveBatch(subscriptions);
    }
} 