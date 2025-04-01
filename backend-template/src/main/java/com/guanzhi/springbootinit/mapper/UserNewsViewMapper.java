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
}