package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.UserOperationLogMapper;
import com.guanzhi.springbootinit.model.entity.UserOperationLog;
import com.guanzhi.springbootinit.service.UserOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
} 