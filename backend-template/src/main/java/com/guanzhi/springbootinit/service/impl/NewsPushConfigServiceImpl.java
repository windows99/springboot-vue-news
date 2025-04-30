package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.NewsPushConfigMapper;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.UserMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsPushConfigDTO;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsPushConfig;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.service.NewsPushConfigService;
import com.guanzhi.springbootinit.service.NewsPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 新闻推送配置服务实现
 */
@Service
@Slf4j
public class NewsPushConfigServiceImpl extends ServiceImpl<NewsPushConfigMapper, NewsPushConfig> implements NewsPushConfigService {

    @Resource
    private NewsPushConfigMapper newsPushConfigMapper;
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private NewsMapper newsMapper;
    
    @Resource
    private NewsPushService newsPushService;

    @Override
    public List<NewsPushConfigDTO> getConfigList() {
        try {
            // 优先使用Mapper中的SQL查询获取详细信息
            List<NewsPushConfigDTO> configList = newsPushConfigMapper.getAllConfigsWithDetails();
            if (configList != null && !configList.isEmpty()) {
                return configList;
            }
            
            // 如果上面的查询失败或返回空，则手动查询和组装数据
            LambdaQueryWrapper<NewsPushConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(NewsPushConfig::getIsDelete, 0)
                    .orderByDesc(NewsPushConfig::getCreateTime);
            
            List<NewsPushConfig> configs = this.list(queryWrapper);
            return configs.stream().map(config -> {
                NewsPushConfigDTO dto = new NewsPushConfigDTO();
                BeanUtils.copyProperties(config, dto);
                
                // 获取用户信息
                if (config.getUserId() != null) {
                    User user = userMapper.selectById(config.getUserId());
                    if (user != null) {
                        dto.setUserName(user.getUserName());
                    }
                }
                
                // 获取新闻信息
                if (config.getNewsId() != null) {
                    News news = newsMapper.selectById(config.getNewsId());
                    if (news != null) {
                        dto.setNewsTitle(news.getTitle());
                    }
                }
                
                return dto;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取推送配置列表失败: {}", e.getMessage());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取推送配置列表失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveConfig(NewsPushConfigDTO configDTO) {
        if (configDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        
        NewsPushConfig config = new NewsPushConfig();
        BeanUtils.copyProperties(configDTO, config);
        
        // 处理推送状态
        if (config.getStatus() == null) {
            config.setStatus(1); // 默认启用
        }
        
        // 计算下一次执行时间
        if (config.getStatus() == 1) {
            Date nextPushTime = calculateNextPushTime(config);
            config.setNextPushTime(nextPushTime);
        }
        
        // 处理创建和更新时间
        Date now = new Date();
        if (config.getCreateTime() == null) {
            config.setCreateTime(now);
        }
        config.setUpdateTime(now);
        
        // 保存或更新
        return saveOrUpdate(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteConfig(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        NewsPushConfig config = getById(id);
        if (config == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "配置不存在");
        }
        
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateConfigStatus(Long id, boolean enabled) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        NewsPushConfig config = getById(id);
        if (config == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "配置不存在");
        }
        
        config.setStatus(enabled ? 1 : 0);
        
        // 如果启用，计算下一次执行时间
        if (enabled) {
            Date nextPushTime = calculateNextPushTime(config);
            config.setNextPushTime(nextPushTime);
        } else {
            config.setNextPushTime(null);
        }
        
        config.setUpdateTime(new Date());
        return updateById(config);
    }

    @Override
    public long addNewsPushConfig(NewsPushConfig newsPushConfig, Long loginUserId) {
        // 参数校验
        if (newsPushConfig == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        
        // 获取用户
        User user = userMapper.selectById(loginUserId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        
        // 验证新闻是否存在
        if (newsPushConfig.getNewsId() != null) {
            News news = newsMapper.selectById(newsPushConfig.getNewsId());
            if (news == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "新闻不存在");
            }
        }
        
        // 设置必要字段
        Date now = new Date();
        newsPushConfig.setCreateTime(now);
        newsPushConfig.setUpdateTime(now);
        newsPushConfig.setIsDelete(0);
        
        // 计算下一次执行时间（如果状态为启用）
        if (newsPushConfig.getStatus() != null && newsPushConfig.getStatus() == 1) {
            Date nextPushTime = calculateNextPushTime(newsPushConfig);
            newsPushConfig.setNextPushTime(nextPushTime);
        }
        
        // 保存到数据库
        boolean success = save(newsPushConfig);
        if (!success) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存配置失败");
        }
        
        return newsPushConfig.getId();
    }

    @Override
    public boolean deleteNewsPushConfig(Long id, Long loginUserId) {
        // 参数校验
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 获取配置信息
        NewsPushConfig config = getById(id);
        if (config == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "配置不存在");
        }
        
        // 执行删除
        return removeById(id);
    }

    @Override
    public boolean updateNewsPushConfig(NewsPushConfig newsPushConfig, Long loginUserId) {
        // 参数校验
        if (newsPushConfig == null || newsPushConfig.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }
        
        // 获取原配置信息
        NewsPushConfig oldConfig = getById(newsPushConfig.getId());
        if (oldConfig == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "配置不存在");
        }
        
        // 更新时间
        newsPushConfig.setUpdateTime(new Date());
        
        // 计算下一次执行时间（如果状态为启用）
        if (newsPushConfig.getStatus() != null && newsPushConfig.getStatus() == 1) {
            Date nextPushTime = calculateNextPushTime(newsPushConfig);
            newsPushConfig.setNextPushTime(nextPushTime);
        }
        
        // 执行更新
        return updateById(newsPushConfig);
    }

    @Override
    public NewsPushConfigDTO getNewsPushConfigById(long id) {
        // 优先使用Mapper中的SQL查询获取详细信息
        NewsPushConfigDTO configDTO = newsPushConfigMapper.getConfigDetailById(id);
        if (configDTO != null) {
            return configDTO;
        }
        
        // 如果上面的查询失败或返回空，则手动查询和组装数据
        NewsPushConfig config = getById(id);
        if (config == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "配置不存在");
        }
        
        NewsPushConfigDTO dto = new NewsPushConfigDTO();
        BeanUtils.copyProperties(config, dto);
        
        // 获取用户信息
        if (config.getUserId() != null) {
            User user = userMapper.selectById(config.getUserId());
            if (user != null) {
                dto.setUserName(user.getUserName());
            }
        }
        
        // 获取新闻信息
        if (config.getNewsId() != null) {
            News news = newsMapper.selectById(config.getNewsId());
            if (news != null) {
                dto.setNewsTitle(news.getTitle());
            }
        }
        
        return dto;
    }

    @Override
    public QueryWrapper<NewsPushConfig> getQueryWrapper(NewsPushConfigDTO newsPushConfigDTO) {
        if (newsPushConfigDTO == null) {
            return new QueryWrapper<>();
        }
        
        QueryWrapper<NewsPushConfig> queryWrapper = new QueryWrapper<>();
        
        Long id = newsPushConfigDTO.getId();
        Long userId = newsPushConfigDTO.getUserId();
        Long newsId = newsPushConfigDTO.getNewsId();
        Integer pushType = newsPushConfigDTO.getPushType();
        Integer status = newsPushConfigDTO.getStatus();
        Date nextPushTime = newsPushConfigDTO.getNextPushTime();
        
        // 根据条件构建查询
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        }
        if (newsId != null) {
            queryWrapper.eq("news_id", newsId);
        }
        if (pushType != null) {
            queryWrapper.eq("push_type", pushType);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        if (nextPushTime != null) {
            queryWrapper.le("next_push_time", nextPushTime);
        }
        
        // 排除已删除
        queryWrapper.eq("is_delete", 0);
        
        return queryWrapper;
    }

    @Override
    public Page<NewsPushConfigDTO> listNewsPushConfigByPage(NewsPushConfigDTO newsPushConfigDTO, long pageNum, long pageSize) {
        if (pageNum <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分页参数错误");
        }
        
        // 构建查询条件
        QueryWrapper<NewsPushConfig> queryWrapper = getQueryWrapper(newsPushConfigDTO);
        // 按更新时间倒序
        queryWrapper.orderByDesc("update_time");
        
        // 执行分页查询
        Page<NewsPushConfig> configPage = page(new Page<>(pageNum, pageSize), queryWrapper);
        
        // 转换为DTO
        List<NewsPushConfigDTO> configDTOList = configPage.getRecords().stream().map(config -> {
            NewsPushConfigDTO dto = new NewsPushConfigDTO();
            BeanUtils.copyProperties(config, dto);
            
            // 获取用户信息
            if (config.getUserId() != null) {
                User user = userMapper.selectById(config.getUserId());
                if (user != null) {
                    dto.setUserName(user.getUserName());
                }
            }
            
            // 获取新闻信息
            if (config.getNewsId() != null) {
                News news = newsMapper.selectById(config.getNewsId());
                if (news != null) {
                    dto.setNewsTitle(news.getTitle());
                }
            }
            
            return dto;
        }).collect(Collectors.toList());
        
        // 构建返回结果
        Page<NewsPushConfigDTO> resultPage = new Page<>(configPage.getCurrent(), configPage.getSize(), configPage.getTotal());
        resultPage.setRecords(configDTOList);
        
        return resultPage;
    }

    @Override
    public List<NewsPushConfigDTO> listUserNewsPushConfigs(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        // 优先使用Mapper中的SQL查询获取详细信息
        List<NewsPushConfigDTO> configList = newsPushConfigMapper.getUserConfigsWithDetails(userId);
        if (configList != null && !configList.isEmpty()) {
            return configList;
        }
        
        // 如果上面的查询失败或返回空，则手动查询和组装数据
        LambdaQueryWrapper<NewsPushConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPushConfig::getUserId, userId)
                .eq(NewsPushConfig::getIsDelete, 0)
                .orderByDesc(NewsPushConfig::getCreateTime);
        
        List<NewsPushConfig> configs = this.list(queryWrapper);
        return configs.stream().map(config -> {
            NewsPushConfigDTO dto = new NewsPushConfigDTO();
            BeanUtils.copyProperties(config, dto);
            
            // 获取用户信息
            User user = userMapper.selectById(config.getUserId());
            if (user != null) {
                dto.setUserName(user.getUserName());
            }
            
            // 获取新闻信息
            if (config.getNewsId() != null) {
                News news = newsMapper.selectById(config.getNewsId());
                if (news != null) {
                    dto.setNewsTitle(news.getTitle());
                }
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int executeScheduledPushTasks() {
        // 获取已启用且下一次执行时间已到的配置
        LambdaQueryWrapper<NewsPushConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPushConfig::getStatus, 1)
                .eq(NewsPushConfig::getIsDelete, 0)
                .le(NewsPushConfig::getNextPushTime, new Date())
                .orderByAsc(NewsPushConfig::getNextPushTime);
        
        List<NewsPushConfig> configList = this.list(queryWrapper);
        if (configList.isEmpty()) {
            log.info("没有待执行的推送任务");
            return 0;
        }
        
        int executedCount = 0;
        Date now = new Date();
        
        for (NewsPushConfig config : configList) {
            try {
                boolean success = executePushTask(config);
                
                // 更新最后执行时间和下一次执行时间
                config.setLastExecuteTime(now);
                
                if (success) {
                    // 计算下一次执行时间
                    Date nextPushTime = calculateNextPushTime(config);
                    config.setNextPushTime(nextPushTime);
                    
                    // 更新配置
                    updateById(config);
                    
                    executedCount++;
                }
            } catch (Exception e) {
                log.error("执行推送任务失败: {}", e.getMessage());
            }
        }
        
        return executedCount;
    }

    @Override
    public boolean activateNewsPushConfig(Long id, Long loginUserId) {
        return updateConfigStatus(id, true);
    }

    @Override
    public boolean pauseNewsPushConfig(Long id, Long loginUserId) {
        return updateConfigStatus(id, false);
    }
    
    /**
     * 执行推送任务
     * 
     * @param config 推送配置
     * @return 是否执行成功
     */
    private boolean executePushTask(NewsPushConfig config) {
        Long userId = config.getUserId();
        Long newsId = config.getNewsId();
        
        if (config.getPushType() == null) {
            log.error("推送类型为空，无法执行");
            return false;
        }
        
        try {
            if (userId != null && newsId != null) {
                // 推送特定新闻给特定用户
                return newsPushService.pushSpecificNewsToUser(userId, newsId);
            } else if (userId != null) {
                // 推送新闻给特定用户
                return newsPushService.pushNewsToUser(userId);
            } else if (newsId != null) {
                // 推送特定新闻给所有用户
                int count = newsPushService.pushSpecificNewsToAllUsers(newsId);
                return count > 0;
            } else {
                // 推送新闻给所有用户
                int count = newsPushService.pushNewsToAllUsers();
                return count > 0;
            }
        } catch (Exception e) {
            log.error("执行推送任务失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 计算下一次推送时间
     * 
     * @param config 推送配置
     * @return 下一次推送时间
     */
    private Date calculateNextPushTime(NewsPushConfig config) {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        
        // 如果没有设置表达式，默认使用当前时间
        if (config.getPushTimeExpression() == null || config.getPushTimeExpression().isEmpty()) {
            return now;
        }
        
        // 按推送类型计算下一次推送时间
        if (config.getPushType() == 0) {
            // 按间隔时间推送，解析分钟数
            try {
                int intervalMinutes = Integer.parseInt(config.getPushTimeExpression());
                calendar.setTime(now);
                calendar.add(Calendar.MINUTE, intervalMinutes);
                return calendar.getTime();
            } catch (NumberFormatException e) {
                log.error("解析间隔时间失败: {}", e.getMessage());
                return now;
            }
        } else if (config.getPushType() == 1) {
            // 按固定时间推送，解析cron表达式
            // 这里简化实现，实际应该使用CronExpression或Quartz等工具解析cron表达式
            // 暂时简单处理，增加一天
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTime();
        }
        
        return now;
    }
} 