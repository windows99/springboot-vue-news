package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.entity.UserNewsView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserNewsViewMapper extends BaseMapper<UserNewsView> {
    
    /**
     * 获取用户最近的浏览记录
     */
    @Select("SELECT * FROM user_news_view WHERE userId = #{userId} ORDER BY viewTime DESC LIMIT #{limit}")
    List<UserNewsView> getUserRecentViews(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 统计用户浏览的新闻类别
     */
    @Select("SELECT n.category, COUNT(*) as count FROM user_news_view v " +
            "JOIN news n ON v.newsId = n.id " +
            "WHERE v.userId = #{userId} AND n.isDelete = 0 " +
            "GROUP BY n.category " +
            "ORDER BY count DESC")
    List<Map<String, Object>> getUserCategoryPreferences(@Param("userId") Long userId);
    
    /**
     * 查找浏览过相似分类的用户
     * 查找与目标用户浏览过相同或相似分类的其他用户，用于协同过滤推荐
     *
     * @param categories 目标用户浏览过的分类列表
     * @param userId 目标用户ID（排除自己）
     * @param limit 返回的最大用户数量
     * @return 相似用户的ID列表
     */
    @Select("<script>SELECT v.userId, COUNT(DISTINCT n.category) AS matches " +
            "FROM user_news_view v " +
            "JOIN news n ON v.newsId = n.id " +
            "WHERE v.userId != #{userId} " +
            "AND n.isDelete = 0 " +
            "AND n.category IN " +
            "<foreach collection='categories' item='category' open='(' separator=',' close=')'>" +
            "#{category}" +
            "</foreach>" +
            "GROUP BY v.userId " +
            "ORDER BY matches DESC " +
            "LIMIT #{limit}</script>")
    List<Long> findUsersBySimilarCategories(
            @Param("categories") List<Long> categories, 
            @Param("userId") Long userId, 
            @Param("limit") Integer limit);
    
    /**
     * 获取相似用户浏览过但当前用户未浏览的新闻
     * 用于协同过滤推荐算法
     *
     * @param similarUserIds 相似用户的ID列表
     * @param excludeNewsIds 需要排除的新闻ID列表（当前用户已浏览过的）
     * @param limit 返回的最大新闻数量
     * @return 推荐新闻的ID列表
     */
    @Select("<script>SELECT DISTINCT v.newsId " +
            "FROM user_news_view v " +
            "JOIN news n ON v.newsId = n.id " +
            "WHERE v.userId IN " +
            "<foreach collection='similarUserIds' item='userId' open='(' separator=',' close=')'>" +
            "#{userId}" +
            "</foreach>" +
            "AND n.status = 3 " +
            "AND n.isDelete = 0 " +
            "<if test='excludeNewsIds != null and excludeNewsIds.size() > 0'>" +
            "AND v.newsId NOT IN " +
            "<foreach collection='excludeNewsIds' item='newsId' open='(' separator=',' close=')'>" +
            "#{newsId}" +
            "</foreach>" +
            "</if>" +
            "GROUP BY v.newsId " +
            "ORDER BY COUNT(v.id) DESC, MAX(v.viewTime) DESC " +
            "LIMIT #{limit}</script>")
    List<Long> getNewsViewedBySimilarUsers(
            @Param("similarUserIds") List<Long> similarUserIds, 
            @Param("excludeNewsIds") List<Long> excludeNewsIds, 
            @Param("limit") Integer limit);
}