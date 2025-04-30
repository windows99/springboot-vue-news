package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsPushDTO;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 新闻推送Mapper
 */
@Mapper
public interface NewsPushMapper extends BaseMapper<NewsPush> {
    
    /**
     * 获取用户的推送历史记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 推送历史列表
     */
    @Select("SELECT p.*, n.title as newsTitle, n.coverImage as newsCoverImage " +
            "FROM news_push p " +
            "LEFT JOIN news n ON p.newsId = n.id " +
            "WHERE p.userId = #{userId} AND p.is_delete = 0 " +
            "ORDER BY p.pushTime DESC " +
            "LIMIT #{limit}")
    List<NewsPushDTO> getUserPushHistory(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 获取用户最近推送的新闻ID列表
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 新闻ID列表
     */
    @Select("SELECT newsId FROM news_push " +
            "WHERE userId = #{userId} AND is_delete = 0 " +
            "ORDER BY pushTime DESC " +
            "LIMIT #{limit}")
    List<Long> getRecentlyPushedNewsIds(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 获取待推送的记录
     * @return 待推送记录列表
     */
    @Select("SELECT * FROM news_push " +
            "WHERE status = 0 AND is_delete = 0 " +
            "AND pushTime <= NOW() " +
            "ORDER BY pushTime ASC")
    List<NewsPush> getPendingPushes();
} 