package com.guanzhi.springbootinit.service;

import com.guanzhi.springbootinit.model.vo.DashboardVO;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {
    /**
     * 获取仪表盘数据
     *
     * @return 仪表盘数据
     */
    DashboardVO getDashboardData();
} 