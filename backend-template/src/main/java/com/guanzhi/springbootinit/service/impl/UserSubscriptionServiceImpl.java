package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.UserSubscriptionMapper;
import com.guanzhi.springbootinit.model.entity.UserSubscription;
import com.guanzhi.springbootinit.service.UserSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 用户订阅服务实现
 *
 * @author sk
 */
@Slf4j
@Service
public class UserSubscriptionServiceImpl extends ServiceImpl<UserSubscriptionMapper, UserSubscription> implements UserSubscriptionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSubscriptions(Long userId, List<Long> tagIds) {
        try {
            // 先删除用户的所有订阅
            this.lambdaUpdate()
                    .eq(UserSubscription::getUserId, userId)
                    .remove();
            
            // 批量插入新的订阅
            if (tagIds != null && !tagIds.isEmpty()) {
                List<UserSubscription> subscriptions = tagIds.stream()
                        .map(category -> {
                            UserSubscription subscription = new UserSubscription();
                            subscription.setUserId(userId);
                            subscription.setCategory(category);
                            return subscription;
                        })
                        .toList();
                return this.saveBatch(subscriptions);
            }
            return true;
        } catch (Exception e) {
            log.error("保存用户订阅失败, userId: {}, tagIds: {}", userId, tagIds, e);
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getUserSubscriptions(Long userId) {
        return baseMapper.getUserSubscriptions(userId);
    }

    @Override
    public boolean isSubscribed(Long userId, String category) {
        return this.lambdaQuery()
                .eq(UserSubscription::getUserId, userId)
                .eq(UserSubscription::getCategory, category)
                .count() > 0;
    }
} 