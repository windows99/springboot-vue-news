package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.UserMapper;
import com.guanzhi.springbootinit.mapper.UserNewsViewMapper;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.vo.DashboardVO;
import com.guanzhi.springbootinit.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仪表盘服务实现类
 */
@Service
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserNewsViewMapper userNewsViewMapper;

    @Override
    public DashboardVO getDashboardData() {
        DashboardVO dashboardVO = new DashboardVO();
        
        // 获取今日开始和结束时间
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        // 1. 总新闻数
        QueryWrapper<News> newsQueryWrapper = new QueryWrapper<>();
        newsQueryWrapper.eq("isDelete", 0);
        dashboardVO.setTotalNews(newsMapper.selectCount(newsQueryWrapper));
        
        // 2. 总用户数
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("isDelete", 0);
        dashboardVO.setTotalUsers(userMapper.selectCount(userQueryWrapper));
        
        // 3. 今日新增新闻数
        QueryWrapper<News> todayNewsQueryWrapper = new QueryWrapper<>();
        todayNewsQueryWrapper.eq("isDelete", 0)
                .between("createTime", todayStart, todayEnd);
        dashboardVO.setTodayNews(newsMapper.selectCount(todayNewsQueryWrapper));
        
        // 4. 今日新增用户数
        QueryWrapper<User> todayUserQueryWrapper = new QueryWrapper<>();
        todayUserQueryWrapper.eq("isDelete", 0)
                .between("createTime", todayStart, todayEnd);
        dashboardVO.setTodayUsers(userMapper.selectCount(todayUserQueryWrapper));
        
        // 5. 新闻分类统计
        List<Map<String, Object>> categoryStats = newsMapper.selectCategoryStats();
        dashboardVO.setCategoryStats(categoryStats);
        
        // 6. 新闻标签统计
        List<Map<String, Object>> tagStats = newsMapper.selectTagStats();
        dashboardVO.setTagStats(tagStats);
        
        // 7. 最近7天新闻发布趋势
        List<Map<String, Object>> newsTrend = newsMapper.selectNewsTrend(7);
        dashboardVO.setNewsTrend(newsTrend);
        
        // 8. 最近7天用户活跃度
        List<Map<String, Object>> userActivity = userNewsViewMapper.selectUserActivity(7);
        dashboardVO.setUserActivity(userActivity);
        
        // 9. 热门新闻排行
        List<Map<String, Object>> hotNews = newsMapper.selectHotNews(10);
        dashboardVO.setHotNews(hotNews);
        
        // 10. 系统运行状态
        Map<String, Object> systemStatus = new HashMap<>();
        systemStatus.put("startTime", new Date());
        systemStatus.put("memoryUsage", getMemoryUsage());
        systemStatus.put("cpuUsage", getCpuUsage());
        dashboardVO.setSystemStatus(systemStatus);
        
        return dashboardVO;
    }

    /**
     * 获取内存使用情况
     */
    private Map<String, Object> getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        Map<String, Object> memoryInfo = new HashMap<>();
        memoryInfo.put("total", totalMemory);
        memoryInfo.put("used", usedMemory);
        memoryInfo.put("free", freeMemory);
        memoryInfo.put("usageRate", (double) usedMemory / totalMemory * 100);
        return memoryInfo;
    }

    /**
     * 获取CPU使用情况
     */
    private Map<String, Object> getCpuUsage() {
        // 这里需要根据实际情况实现CPU使用率的获取
        // 可以使用操作系统命令或第三方库
        Map<String, Object> cpuInfo = new HashMap<>();
        cpuInfo.put("usageRate", 0.0); // 示例值
        return cpuInfo;
    }
} 