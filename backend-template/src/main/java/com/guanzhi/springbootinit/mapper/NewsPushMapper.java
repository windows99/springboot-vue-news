package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * 新闻推送记录 Mapper
 */
@Mapper
public interface NewsPushMapper extends BaseMapper<NewsPush> {

    /**
     * 获取用户未读的推送消息
     * @param userId 用户ID
     * @return 未读推送列表
     */
    @Select("SELECT * FROM news_push WHERE userId = #{userId} AND isRead = 0 AND isDelete = 0 ORDER BY pushTime DESC")
    List<NewsPush> getUnreadPushes(@Param("userId") Long userId);
    
    /**
     * 标记消息为已读
     * @param pushId 推送ID
     * @return 影响的行数
     */
    @Update("UPDATE news_push SET isRead = 1, readTime = NOW() WHERE id = #{pushId}")
    int markAsRead(@Param("pushId") Long pushId);

    /**
     * 批量插入推送记录
     *
     * @param pushList 推送记录列表
     * @return 插入成功的记录数
     */
    int insertBatch(@Param("list") List<NewsPush> pushList);

    /**
     * 获取用户最近浏览的新闻ID列表
     */
    @Select("SELECT newsId FROM news_push " +
            "WHERE userId = #{userId} AND isRead = 1 AND isDelete = 0 " +
            "ORDER BY readTime DESC LIMIT #{limit}")
    List<Long> getRecentViewedNewsIds(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取用户最近浏览的新闻及其标签
     */
    @Select("SELECT np.newsId, n.category as tagId, np.readTime " +
            "FROM news_push np " +
            "JOIN news n ON np.newsId = n.id " +
            "WHERE np.userId = #{userId} AND np.isRead = 1 AND np.isDelete = 0 " +
            "ORDER BY np.readTime DESC LIMIT #{limit}")
    List<Map<String, Object>> getRecentViewedNewsWithTags(@Param("userId") Long userId, @Param("limit") Integer limit);
} 