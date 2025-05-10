package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.model.entity.UserOperationLog;

import java.util.List;

/**
 * 用户操作日志服务
 *
 * @author sk
 */
public interface UserOperationLogService extends IService<UserOperationLog> {
    /**
     * 记录用户操作
     *
     * @param userId 用户ID
     * @param operationType 操作类型
     * @param targetId 目标ID
     * @param targetType 目标类型
     * @param operationDetail 操作详情
     */
    void logOperation(Long userId, String operationType, Long targetId, String targetType, String operationDetail);

    /**
     * 获取用户操作历史
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 操作历史列表
     */
    List<UserOperationLog> getUserOperationHistory(Long userId, int limit);

    /**
     * 获取操作历史记录（分页）
     *
     * @param userId 用户ID（可选）
     * @param operationType 操作类型（可选）
     * @param targetType 目标类型（可选）
     * @param current 当前页码
     * @param pageSize 每页大小
     * @return 分页的操作历史记录
     */
    Page<UserOperationLog> getOperationHistory(Long userId, String operationType, String targetType, long current, long pageSize);
} 