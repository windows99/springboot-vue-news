package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.UserSubscriptionMapper;
import com.guanzhi.springbootinit.model.entity.UserSubscription;
import com.guanzhi.springbootinit.service.UserSubscriptionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户订阅服务实现类
 *
 * @author sk
 */
@Service
public class UserSubscriptionServiceImpl extends ServiceImpl<UserSubscriptionMapper, UserSubscription>
        implements UserSubscriptionService {

    @Override
    public boolean addSubscription(Long userId, String category) {
        // 检查是否已经订阅
        if (isSubscribed(userId, category)) {
            return true;
        }

        // 创建新的订阅记录
        UserSubscription subscription = new UserSubscription();
        subscription.setUserId(userId);
        subscription.setCategory(category);
        subscription.setStatus(1); // 1-订阅中

        return save(subscription);
    }

    @Override
    public boolean cancelSubscription(Long userId, String category) {
        QueryWrapper<UserSubscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("category", category);
        queryWrapper.eq("status", 1); // 只查找订阅中的记录

        UserSubscription subscription = getOne(queryWrapper);
        if (subscription == null) {
            return false;
        }

        subscription.setStatus(0); // 0-取消
        return updateById(subscription);
    }

    @Override
    public List<String> getUserSubscriptions(Long userId) {
        QueryWrapper<UserSubscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("status", 1); // 只查找订阅中的记录

        List<UserSubscription> subscriptions = list(queryWrapper);
        return subscriptions.stream()
                .map(UserSubscription::getCategory)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isSubscribed(Long userId, String category) {
        QueryWrapper<UserSubscription> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("category", category);
        queryWrapper.eq("status", 1); // 只查找订阅中的记录

        return count(queryWrapper) > 0;
    }
} 