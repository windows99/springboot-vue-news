package com.guanzhi.springbootinit.model.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘数据视图对象
 */
@Data
public class DashboardVO {
    /**
     * 总新闻数
     */
    private Long totalNews;

    /**
     * 总用户数
     */
    private Long totalUsers;

    /**
     * 今日新增新闻数
     */
    private Long todayNews;

    /**
     * 今日新增用户数
     */
    private Long todayUsers;

    /**
     * 新闻分类统计
     */
    private List<Map<String, Object>> categoryStats;

    /**
     * 新闻标签统计
     */
    private List<Map<String, Object>> tagStats;

    /**
     * 最近7天新闻发布趋势
     */
    private List<Map<String, Object>> newsTrend;

    /**
     * 最近7天用户活跃度
     */
    private List<Map<String, Object>> userActivity;

    /**
     * 热门新闻排行（浏览量）
     */
    private List<Map<String, Object>> hotNews;

    /**
     * 系统运行状态
     */
    private Map<String, Object> systemStatus;
} 