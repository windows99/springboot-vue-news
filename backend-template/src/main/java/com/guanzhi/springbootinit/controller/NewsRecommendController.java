package com.guanzhi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.service.NewsRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/news/recommend")
@Slf4j
public class NewsRecommendController {

    @Autowired
    private NewsRecommendService newsRecommendService;

    /**
     * 获取个性化推荐新闻（分页）
     */
    @GetMapping("/forUser/{userId}")
    public BaseResponse<Page<News>> getRecommendForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID不合法");
        }
        
        Page<News> page = newsRecommendService.getPersonalizedNewsPage(userId, current, pageSize);
        return ResultUtils.success(page);
    }
    
    /**
     * 获取热门新闻（分页）
     */
    @GetMapping("/hot")
    public BaseResponse<Page<News>> getHotNews(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long pageSize) {
        Page<News> page = newsRecommendService.getHotNewsPage(current, pageSize);
        return ResultUtils.success(page);
    }
} 