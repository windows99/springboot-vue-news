package com.guanzhi.springbootinit.service.impl;

import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.constant.CommonConstant;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.news.NewsQueryRequest;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.service.NewsService;
import com.guanzhi.springbootinit.utils.SqlUtils;
import com.obs.services.internal.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public QueryWrapper<News> getQueryWrapper(NewsQueryRequest request) {

        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        System.out.println(request);
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0)
                .eq(request.getCategory() != null, "category", request.getCategory())
                .eq(request.getStatus() != null, "status", request.getStatus())
                .like(StringUtils.isNotBlank(request.getTitle()), "title", request.getTitle())
                .like(StringUtils.isNotBlank(request.getAuthor()), "author", request.getAuthor())
                .orderBy(SqlUtils.validSortField(request.getSortField()),
                        request.getSortOrder().equals(CommonConstant.SORT_ORDER_ASC),
                        StringUtils.isBlank(request.getSortField()) ? "createTime" : request.getSortField());
        System.out.println(queryWrapper);
        return queryWrapper;
    }

//    @Override
//    public List<News> getNewsList(Map<String, Object> params) {
//        try {
//            NewsQueryRequest request;
//            QueryWrapper<News> queryWrapper = new QueryWrapper<>();
//
//            Integer currentPage = params.get("page") == null ? 1 : (Integer) params.get("page");
//            Integer currentPageSize = params.get("pageSize") == null ? 10 : (Integer) params.get("pageSize");
//            Page<News> page = new Page<>(currentPage, currentPageSize);
//
//            Page<News> resultPage = newsMapper.selectPage(page, queryWrapper);
//
//            // 从结果页中获取数据列表，作为返回结果
//            List<News> newsList = resultPage.getRecords();
//
//            return newsList;
//
//        } catch (Exception e) {
//            log.error("Failed to fetch news list", e);
//            throw new RuntimeException("Failed to fetch news list", e);
//        }
//    }

    @Override
    public News getNewsById(Long newsId){
        return newsMapper.selectById(newsId);
    }


    @Override
    public boolean addNews(News news) {

        try {
            int result = newsMapper.insert(news);
            return result > 0;
        } catch (Exception e) {
            log.error("Failed to insert news: ", e);
            throw new RuntimeException("Failed to insert news", e);
        }
    }

    @Override
    public void updateNews(News news) {
        try {
            System.out.println("修改功能");
            System.out.println(news);
            System.out.println("修改功能");
            newsMapper.updateById(news);
        } catch (Exception e) {
            log.error("Failed to update news: {}", e);
            throw new RuntimeException("Failed to update news", e);
        }
    }

    @Override
    public void deleteNewsById(Long newsId) {
        try {
            int result = newsMapper.deleteById(newsId);
            if (result == 0) {
                throw new ServiceException("Failed to delete news with id: " + newsId);
            }
        } catch (Exception e) {
            log.error("Delete news error: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateNewsStatus(Long newsId, Integer status) {
        try {
            News news = new News();
            news.setId(newsId);
            news.setStatus(status);
            newsMapper.updateById(news);
        } catch (Exception e) {
            log.error("Failed to update news status for id: {}", newsId, e);
            throw new RuntimeException("Failed to update news status", e);
        }
    }

    @Override
    public void publishNews(Long newsId) {
        updateNewsStatus(newsId, 1);
    }

    @Override
    public void shelfNews(Long newsId) {
        updateNewsStatus(newsId, 0);
    }
}
