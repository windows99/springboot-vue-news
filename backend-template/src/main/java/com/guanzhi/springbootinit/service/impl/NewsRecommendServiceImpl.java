package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.NewsTagMapper;
import com.guanzhi.springbootinit.mapper.UserNewsViewMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsRecommendDTO;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsTag;
import com.guanzhi.springbootinit.service.NewsRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsRecommendServiceImpl implements NewsRecommendService {

    @Resource
    private UserNewsViewMapper userNewsViewMapper;
    
    @Resource
    private NewsMapper newsMapper;
    
    @Resource
    private NewsTagMapper newsTagMapper;

    @Override
    public Page<NewsRecommendDTO> getPersonalizedNewsPage(Long userId, long current, long pageSize) {
        // 获取所有状态为3的新闻总数
        LambdaQueryWrapper<News> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(News::getStatus, 3).eq(News::getIsdelete, 0);  // 只统计状态为3的新闻总数
        long total = newsMapper.selectCount(countWrapper);
        
        // 构建分页查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)  // 状态为3的新闻
                   .eq(News::getIsdelete, 0) // 未删除的新闻
                   .orderByDesc(News::getCreatetime); // 按创建时间降序
        
        // TODO: 这里可以根据用户的兴趣标签、浏览历史等进行个性化推荐
        // 当前简单实现，后续可以接入推荐算法
        
        // 执行分页查询
        Page<News> newsPage = newsMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
        
        // 转换为DTO对象
        Page<NewsRecommendDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(newsPage, dtoPage, "records");
        dtoPage.setTotal(total); // 设置实际总数
        
        List<NewsRecommendDTO> dtoList = newsPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public Page<NewsRecommendDTO> getHotNewsPage(long current, long pageSize) {
        // 获取所有状态为3的新闻总数
        LambdaQueryWrapper<News> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(News::getStatus, 3);  // 只统计状态为3的新闻总数
        long total = newsMapper.selectCount(countWrapper);
        
        // 构建查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)  // 状态为3的新闻
                   .eq(News::getIsdelete, 0) // 未删除的新闻
                   .orderByDesc(News::getViewcount, News::getCreatetime); // 按浏览量和创建时间降序
        
        // 执行分页查询
        Page<News> newsPage = newsMapper.selectPage(new Page<>(current, pageSize), queryWrapper);
        
        // 转换为DTO对象
        Page<NewsRecommendDTO> dtoPage = new Page<>();
        BeanUtils.copyProperties(newsPage, dtoPage, "records");
        dtoPage.setTotal(total); // 设置实际总数
        
        List<NewsRecommendDTO> dtoList = newsPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    public List<NewsRecommendDTO> getPersonalizedNews(Long userId, Integer limit) {
        // 构建查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)  // 状态为3的新闻
                   .eq(News::getIsdelete, 0) // 未删除的新闻
                   .orderByDesc(News::getCreatetime) // 按创建时间降序
                   .last("LIMIT " + limit); // 限制返回数量
        
        // TODO: 这里可以根据用户的兴趣标签、浏览历史等进行个性化推荐
        
        List<News> newsList = newsMapper.selectList(queryWrapper);
        return newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NewsRecommendDTO> getHotNews(Integer limit) {
        // 构建查询条件
        LambdaQueryWrapper<News> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(News::getStatus, 3)  // 状态为3的新闻
                   .eq(News::getIsdelete, 0) // 未删除的新闻
                   .orderByDesc(News::getViewcount, News::getCreatetime) // 按浏览量和创建时间降序
                   .last("LIMIT " + limit); // 限制返回数量
        
        List<News> newsList = newsMapper.selectList(queryWrapper);
        return newsList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将News实体转换为NewsRecommendDTO
     */
    private NewsRecommendDTO convertToDTO(News news) {
        NewsRecommendDTO dto = new NewsRecommendDTO();
        BeanUtils.copyProperties(news, dto);
        
        // 获取分类名称
        if (news.getCategory() != null) {
            NewsTag newsTag = newsTagMapper.selectById(news.getCategory());
            if (newsTag != null) {
                dto.setCategoryName(newsTag.getTagname());
            }
        }
        
        return dto;
    }
} 