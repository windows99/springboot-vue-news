package com.guanzhi.springbootinit.controller;

import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.model.vo.DashboardVO;
import com.guanzhi.springbootinit.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 仪表盘接口
 */
@RestController
@RequestMapping("/dashboard")
@Slf4j
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/data")
    public BaseResponse<DashboardVO> getDashboardData(HttpServletRequest request) {
        DashboardVO dashboardData = dashboardService.getDashboardData();
        return ResultUtils.success(dashboardData);
    }
} 