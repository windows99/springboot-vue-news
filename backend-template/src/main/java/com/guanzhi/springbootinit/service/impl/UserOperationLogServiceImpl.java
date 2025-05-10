package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.UserOperationLogMapper;
import com.guanzhi.springbootinit.model.entity.UserOperationLog;
import com.guanzhi.springbootinit.service.UserOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户操作日志服务实现
 *
 * @author sk
 */
@Slf4j
@Service
public class UserOperationLogServiceImpl extends ServiceImpl<UserOperationLogMapper, UserOperationLog> implements UserOperationLogService {
    @Override
    public void logOperation(Long userId, String operationType, Long targetId, String targetType, String operationDetail) {
        UserOperationLog log = new UserOperationLog();
        log.setUserId(userId);
        log.setOperationType(operationType);
        log.setTargetId(targetId);
        log.setTargetType(targetType);
        log.setOperationDetail(operationDetail);
        
        // 设置时间
        Date now = new Date();
        log.setOperationTime(now);
        log.setCreateTime(now);
        log.setUpdateTime(now);
        
        // 设置默认值
        log.setIsDelete(0);
        
        // 保存日志
        this.save(log);
    }

    @Override
    public List<UserOperationLog> getUserOperationHistory(Long userId, int limit) {
        return this.lambdaQuery()
                .eq(UserOperationLog::getUserId, userId)
                .eq(UserOperationLog::getIsDelete, 0)
                .orderByDesc(UserOperationLog::getOperationTime)
                .last("LIMIT " + limit)
                .list();
    }

    @Override
    public Page<UserOperationLog> getOperationHistory(Long userId, String operationType, String targetType, long current, long pageSize) {
        Page<UserOperationLog> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<UserOperationLog> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        queryWrapper.eq(userId != null, UserOperationLog::getUserId, userId)
                .eq(StringUtils.isNotBlank(operationType), UserOperationLog::getOperationType, operationType)
                .eq(StringUtils.isNotBlank(targetType), UserOperationLog::getTargetType, targetType)
                .eq(UserOperationLog::getIsDelete, 0)
                .orderByDesc(UserOperationLog::getOperationTime);
        
        return this.page(page, queryWrapper);
    }
} 