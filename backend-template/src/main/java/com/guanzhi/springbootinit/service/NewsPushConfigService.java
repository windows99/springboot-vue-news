package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.dto.news.NewsPushConfigDTO;
import com.guanzhi.springbootinit.model.entity.NewsPushConfig;

import java.util.Date;
import java.util.List;

/**
 * 新闻推送配置服务接口
 */
public interface NewsPushConfigService extends IService<NewsPushConfig> {

    /**
     * 获取所有推送配置列表
     * 
     * @return 推送配置列表
     */
    List<NewsPushConfigDTO> getConfigList();
    
    /**
     * 保存(添加或更新)推送配置
     * 
     * @param config 配置信息
     * @return 是否成功
     */
    boolean saveConfig(NewsPushConfigDTO config);
    
    /**
     * 删除推送配置
     * 
     * @param id 配置ID
     * @return 是否成功
     */
    boolean deleteConfig(Long id);
    
    /**
     * 更新推送配置状态
     * 
     * @param id 配置ID
     * @param enabled 是否启用
     * @return 是否成功
     */
    boolean updateConfigStatus(Long id, boolean enabled);

    /**
     * 创建新闻推送配置
     *
     * @param newsPushConfig
     * @param loginUser
     * @return
     */
    long addNewsPushConfig(NewsPushConfig newsPushConfig, Long loginUserId);

    /**
     * 删除新闻推送配置
     *
     * @param deleteRequest
     * @param loginUser
     * @return
     */
    boolean deleteNewsPushConfig(Long id, Long loginUserId);

    /**
     * 更新新闻推送配置
     *
     * @param newsPushConfig
     * @param loginUser
     * @return
     */
    boolean updateNewsPushConfig(NewsPushConfig newsPushConfig, Long loginUserId);

    /**
     * 根据 id 获取新闻推送配置
     *
     * @param id
     * @return
     */
    NewsPushConfigDTO getNewsPushConfigById(long id);

    /**
     * 获取查询条件
     *
     * @param newsPushConfigDTO
     * @return
     */
    QueryWrapper<NewsPushConfig> getQueryWrapper(NewsPushConfigDTO newsPushConfigDTO);

    /**
     * 分页获取新闻推送配置
     *
     * @param newsPushConfigDTO
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<NewsPushConfigDTO> listNewsPushConfigByPage(NewsPushConfigDTO newsPushConfigDTO, long pageNum, long pageSize);

    /**
     * 获取用户的推送配置列表
     * 
     * @param userId
     * @return
     */
    List<NewsPushConfigDTO> listUserNewsPushConfigs(Long userId);
    
    /**
     * 执行定时推送任务
     * 
     * @return 执行的任务数量
     */
    int executeScheduledPushTasks();
    
    /**
     * 激活推送配置
     * 
     * @param id 配置ID
     * @param loginUserId 登录用户ID
     * @return 是否成功
     */
    boolean activateNewsPushConfig(Long id, Long loginUserId);
    
    /**
     * 暂停推送配置
     * 
     * @param id 配置ID
     * @param loginUserId 登录用户ID
     * @return 是否成功
     */
    boolean pauseNewsPushConfig(Long id, Long loginUserId);
} 