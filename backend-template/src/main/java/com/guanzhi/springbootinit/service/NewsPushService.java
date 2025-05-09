package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.model.dto.NewsPushDTO;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import com.guanzhi.springbootinit.model.vo.NewsPushVO;
import java.util.List;

/**
 * 新闻推送服务
 */
public interface NewsPushService {

    /**
     * 立即推送新闻
     *
     * @param newsId 新闻ID
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    boolean pushImmediately(Long newsId, List<Long> userIds);

    /**
     * 批量推送多条新闻
     *
     * @param newsIds 新闻ID列表
     * @param userIds 用户ID列表
     * @return 成功推送的数量
     */
    int pushMultipleNews(List<Long> newsIds, List<Long> userIds);

    /**
     * 个性化推送新闻给指定用户
     *
     * @param userId 用户ID
     * @param limit 推送数量限制
     * @return 成功推送的数量
     */
    int pushPersonalizedNews(Long userId, Integer limit);

    /**
     * 获取未读推送消息
     *
     * @param userId 用户ID
     * @return 未读消息列表
     */
    List<NewsPushDTO> getUnreadPushes(Long userId);

    /**
     * 标记推送为已读
     *
     * @param pushId 推送ID
     * @return 是否成功
     */
    boolean markAsRead(Long pushId);

    /**
     * 获取用户的推送记录列表
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param pageSize 每页大小
     * @param isRead 是否已读（可选）
     * @return 推送记录分页数据
     */
    Page<NewsPush> getUserPushRecords(Long userId, long current, long pageSize, Integer isRead);

    /**
     * 获取推送记录详情
     *
     * @param recordId 记录ID
     * @param userId 用户ID
     * @return 推送记录详情
     */
    NewsPush getPushRecordDetail(Long recordId, Long userId);

    /**
     * 获取用户的推送记录列表（包含新闻标题）
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param pageSize 每页大小
     * @param isRead 是否已读（可选）
     * @return 推送记录分页数据
     */
    Page<NewsPushVO> getUserPushRecordsWithNews(Long userId, long current, long pageSize, Integer isRead);
} 