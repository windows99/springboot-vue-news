package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.dto.news.NewsPushDTO;
import com.guanzhi.springbootinit.model.vo.NewsPushVO;
import com.guanzhi.springbootinit.model.entity.NewsPush;

import java.util.Date;
import java.util.List;

/**
 * 新闻推送服务接口
 */
public interface NewsPushService extends IService<NewsPush> {

    /**
     * 向所有用户推送最新新闻
     * @return 成功推送的数量
     */
    int pushNewsToAllUsers();

    /**
     * 向特定用户推送最新新闻
     * @param userId 用户ID
     * @return 推送结果
     */
    boolean pushNewsToUser(Long userId);

    /**
     * 推送特定新闻给所有用户
     * @param newsId 新闻ID
     * @return 成功推送的数量
     */
    int pushSpecificNewsToAllUsers(Long newsId);

    /**
     * 推送特定新闻给特定用户
     * @param userId 用户ID
     * @param newsId 新闻ID
     * @return 推送结果
     */
    boolean pushSpecificNewsToUser(Long userId, Long newsId);

    /**
     * 批量推送多个新闻给指定用户
     * @param userId 用户ID
     * @param newsIds 新闻ID列表
     * @return 成功推送的数量
     */
    int pushMultipleNewsToUser(Long userId, List<Long> newsIds);

    /**
     * 批量推送多个新闻给所有用户
     * @param newsIds 新闻ID列表
     * @return 成功推送的总次数
     */
    int pushMultipleNewsToAllUsers(List<Long> newsIds);

    /**
     * 为指定用户设置定时推送
     * @param userId 用户ID
     * @param pushTime 推送时间
     * @return 是否成功
     */
    boolean schedulePushToUser(Long userId, Date pushTime);

    /**
     * 为所有用户设置定时推送
     * @param pushTime 推送时间
     * @return 成功设置的用户数
     */
    int schedulePushToAllUsers(Date pushTime);

    /**
     * 执行所有待处理的推送任务
     * @return 成功执行的任务数
     */
    int executePendingPushes();

    /**
     * 获取用户的推送历史
     * @param userId 用户ID
     * @return 推送历史列表(VO)
     */
    List<NewsPushVO> getUserPushHistory(Long userId);

    /**
     * 获取用户的推送历史(带限制条数)
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 推送历史列表(DTO)
     */
    List<NewsPushDTO> getUserPushHistory(Long userId, int limit);

    /**
     * 标记推送为已读
     * @param pushId 推送ID
     * @param userId 用户ID（验证所有权）
     * @return 操作结果
     */
    boolean markPushAsRead(Long pushId, Long userId);

    /**
     * 标记推送为已读（不验证所有权）
     * @param pushId 推送ID
     * @return 操作结果
     */
    boolean markPushAsRead(Long pushId);

    /**
     * 删除推送记录
     * @param pushId 推送ID
     * @param userId 用户ID（验证所有权）
     * @return 操作结果
     */
    boolean deletePush(Long pushId, Long userId);

    /**
     * 获取用户未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    int getUnreadCount(Long userId);

    /**
     * 获取用户最近推送的新闻ID列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 新闻ID列表
     */
    List<Long> getRecentlyPushedNewsIds(Long userId, int limit);
} 