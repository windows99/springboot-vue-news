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
     * 立即推送新闻给指定用户
     *
     * @param newsIds 新闻ID列表
     * @param userIds 用户ID列表
     * @return 是否推送成功
     */
    boolean pushImmediately(List<Long> newsIds, List<Long> userIds);

    /**
     * 批量推送新闻给指定用户
     *
     * @param newsIds 新闻ID列表
     * @param userIds 用户ID列表
     * @return 成功推送的数量
     */
    int pushMultipleNews(List<Long> newsIds, List<Long> userIds);

    /**
     * 推送个性化新闻给用户
     *
     * @param userId 用户ID
     * @param limit 推送数量限制
     * @return 推送的新闻数量
     */
    int pushPersonalizedNews(Long userId, Integer limit);

    /**
     * 获取用户的未读推送
     *
     * @param userId 用户ID
     * @return 未读推送列表
     */
    List<NewsPushDTO> getUnreadPushes(Long userId);

    /**
     * 标记推送为已读
     *
     * @param pushId 推送ID
     * @return 是否标记成功
     */
    boolean markAsRead(Long pushId);

    /**
     * 获取用户的推送记录
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param pageSize 每页大小
     * @param isRead 是否已读（null表示全部）
     * @return 推送记录分页
     */
    Page<NewsPush> getUserPushRecords(Long userId, long current, long pageSize, Integer isRead);

    /**
     * 获取推送记录详情
     *
     * @param recordId 记录ID
     * @param userId 用户ID
     * @return 推送记录
     */
    NewsPush getPushRecordDetail(Long recordId, Long userId);

    /**
     * 获取用户的推送记录（包含新闻信息）
     *
     * @param userId 用户ID
     * @param current 当前页码
     * @param pageSize 每页大小
     * @param isRead 是否已读（null表示全部）
     * @return 推送记录分页（包含新闻信息）
     */
    Page<NewsPushVO> getUserPushRecordsWithNews(Long userId, long current, long pageSize, Integer isRead);

    /**
     * 推送新闻给指定用户
     *
     * @param newsId 新闻ID
     * @param userId 用户ID
     * @return 是否推送成功
     */
    boolean pushNewsToUser(Long newsId, Long userId);
} 