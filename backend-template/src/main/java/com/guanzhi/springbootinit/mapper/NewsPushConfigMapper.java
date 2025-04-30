package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsPushConfigDTO;
import com.guanzhi.springbootinit.model.entity.NewsPushConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 新闻推送配置Mapper
 */
@Mapper
public interface NewsPushConfigMapper extends BaseMapper<NewsPushConfig> {
    
    /**
     * 获取所有推送配置列表
     * @return 推送配置列表
     */
    @Select("SELECT c.*, u.userName as userName, n.title as newsTitle " +
            "FROM news_push_config c " +
            "LEFT JOIN user u ON c.userId = u.id " +
            "LEFT JOIN news n ON c.newsId = n.id " +
            "WHERE c.is_delete = 0 " +
            "ORDER BY c.createTime DESC")
    List<NewsPushConfigDTO> getAllConfigsWithDetails();
    
    /**
     * 获取用户的推送配置列表
     * @param userId 用户ID
     * @return 推送配置列表
     */
    @Select("SELECT c.*, u.userName as userName, n.title as newsTitle " +
            "FROM news_push_config c " +
            "LEFT JOIN user u ON c.userId = u.id " +
            "LEFT JOIN news n ON c.newsId = n.id " +
            "WHERE c.userId = #{userId} AND c.is_delete = 0 " +
            "ORDER BY c.createTime DESC")
    List<NewsPushConfigDTO> getUserConfigsWithDetails(@Param("userId") Long userId);
    
    /**
     * 获取待执行的推送配置
     * @return 待执行的推送配置列表
     */
    @Select("SELECT c.*, u.userName as userName, n.title as newsTitle " +
            "FROM news_push_config c " +
            "LEFT JOIN user u ON c.userId = u.id " +
            "LEFT JOIN news n ON c.newsId = n.id " +
            "WHERE c.status = 1 AND c.is_delete = 0 " +
            "ORDER BY c.nextPushTime ASC")
    List<NewsPushConfigDTO> getPendingConfigs();
    
    /**
     * 获取指定ID的推送配置详情
     * @param id 配置ID
     * @return 推送配置详情
     */
    @Select("SELECT c.*, u.userName as userName, n.title as newsTitle " +
            "FROM news_push_config c " +
            "LEFT JOIN user u ON c.userId = u.id " +
            "LEFT JOIN news n ON c.newsId = n.id " +
            "WHERE c.id = #{id} AND c.is_delete = 0")
    NewsPushConfigDTO getConfigDetailById(@Param("id") Long id);
} 