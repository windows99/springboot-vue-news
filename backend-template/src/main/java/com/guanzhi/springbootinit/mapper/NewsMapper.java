package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.entity.News;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


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
            "WHERE n.isDelete = 0 AND n.status = 1 " +
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
            "n.createtime DESC " +
            "LIMIT #{limit}" +
            "</script>")
    List<News> getNewsByTagsWithWeights(@Param("tagIds") List<Long> tagIds,
                                      @Param("tagWeights") Map<Long, Integer> tagWeights,
                                      @Param("excludeNewsIds") List<Long> excludeNewsIds,
                                      @Param("limit") Integer limit);

}