package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.entity.News;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 新闻 Mapper
 */
@Mapper
public interface NewsMapper extends BaseMapper<News> {

    /**
     * 根据标签获取新闻
     */
    @Select("<script>" +
            "SELECT * FROM news WHERE category IN " +
            "<foreach collection='tagIds' item='tagId' open='(' separator=',' close=')'>" +
            "#{tagId}" +
            "</foreach>" +
            " AND status = 3 AND isDelete = 0 ORDER BY createTime DESC LIMIT #{limit}" +
            "</script>")
    List<News> getNewsByTags(@Param("tagIds") List<Long> tagIds, @Param("limit") int limit);

    /**
     * 根据标签和权重获取新闻
     */
    @Select("<script>" +
            "SELECT DISTINCT n.* FROM news n " +
            "WHERE n.isDelete = 0 AND n.status IN (1, 2, 3) " +
            "AND n.category IN " +
            "<foreach collection='tagIds' item='tagId' open='(' separator=',' close=')'>" +
            "#{tagId}" +
            "</foreach>" +
            "<if test='excludeNewsIds != null and excludeNewsIds.size() > 0'>" +
            "AND n.id NOT IN " +
            "<foreach collection='excludeNewsIds' item='newsId' open='(' separator=',' close=')'>" +
            "#{newsId}" +
            "</foreach>" +
            "</if>" +
            "ORDER BY " +
            "<if test='tagWeights != null and tagWeights.size() > 0'>" +
            "CASE n.category " +
            "<foreach collection='tagWeights' item='weight' index='tagId'>" +
            "WHEN #{tagId} THEN #{weight} " +
            "</foreach>" +
            "ELSE 0 END DESC, " +
            "</if>" +
            "n.createTime DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<News> getNewsByTagsWithWeights(@Param("tagIds") List<Long> tagIds,
                                      @Param("tagWeights") Map<Long, Integer> tagWeights,
                                      @Param("excludeNewsIds") List<Long> excludeNewsIds,
                                      @Param("limit") Integer limit);

    /**
     * 获取新闻分类统计
     */
    @Select("SELECT t.tagName as categoryName, COUNT(n.id) as count " +
            "FROM news n " +
            "LEFT JOIN news_tag t ON n.category = t.id " +
            "WHERE n.isDelete = 0 " +
            "GROUP BY n.category, t.tagName")
    List<Map<String, Object>> selectCategoryStats();

    /**
     * 获取新闻标签统计
     */
    @Select("SELECT t.tagName as tagName, COUNT(us.id) as count " +
            "FROM news_tag t " +
            "LEFT JOIN user_subscription us ON t.id = us.category " +
            "LEFT JOIN news n ON us.category = n.category " +
            "WHERE n.isDelete = 0 " +
            "GROUP BY t.id, t.tagName")
    List<Map<String, Object>> selectTagStats();

    /**
     * 获取最近N天新闻发布趋势
     */
    @Select("SELECT DATE(createTime) as date, COUNT(*) as count " +
            "FROM news " +
            "WHERE isDelete = 0 " +
            "AND createTime >= DATE_SUB(CURDATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(createTime) " +
            "ORDER BY date")
    List<Map<String, Object>> selectNewsTrend(int days);

    /**
     * 获取热门新闻排行
     */
    @Select("SELECT id, title, viewCount, likeCount, commentCount " +
            "FROM news " +
            "WHERE isDelete = 0 " +
            "ORDER BY viewCount DESC " +
            "LIMIT #{limit}")
    List<Map<String, Object>> selectHotNews(int limit);
}