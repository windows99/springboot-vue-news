package com.guanzhi.springbootinit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.guanzhi.springbootinit.service.SensitiveWordService;
import com.guanzhi.springbootinit.utils.HttpUtils;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Autowired
    private SensitiveWordService sensitiveWordService;

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
                .orderByDesc(StringUtils.isBlank(request.getSortField()) ? "createTime" : request.getSortField());
        System.out.println(queryWrapper);
        return queryWrapper;
    }



    @Override
    public News getNewsById(Long newsId){
        News news = newsMapper.selectById(newsId);
        if (news == null) {
            log.error("Failed to increment view count for news with id: {}");
        }
        news.setViewcount(news.getViewcount() + 1);

        try {
            newsMapper.updateById(news);
        } catch (Exception e) {
            log.error("Failed to increment view count for news with id: {}", newsId, e);
            throw new RuntimeException("Failed to update view count", e);
        }
        // 将news的view_count加1，返回最新值
        return news;
    }


    @Override
    public boolean addNews(News news) {

        try {
//            if (sensitiveWordService.checkContentForSensitive(news.getContent())) {
//                throw new BusinessException(ErrorCode.SENSITIVE_WORDS_FOUND, "内容包含敏感词");
//            }
            int result =  newsMapper.insert(news);
            return result > 0;
        } catch (BusinessException be) {
//            log.info("发布新闻失败，原因：{}", be);
            throw be;
        } catch (Exception e) {
            log.error("发布新闻失败", e);
            throw new RuntimeException("Failed to publish news", e);
        }


//        try {
//            int result = newsMapper.insert(news);
//            return result > 0;
//        } catch (Exception e) {
//            log.error("Failed to insert news: ", e);
//            throw new RuntimeException("Failed to insert news", e);
//        }
    }

//    @Override
//    public boolean batchAddNews(List<News> newsList) {
//        try {
//            if (newsList == null || newsList.isEmpty()) {
//                log.warn("空的新闻列表，未进行任何操作");
//                return false;
//            }
//
//            int result = newsMapper.saveBatch (newsList);
//            if (result > 0) {
//                log.info("成功批量添加{}条新闻", result);
//            } else {
//                log.error("批量添加新闻失败，结果码: {}", result);
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to add news in batch");
//            }
//        } catch (Exception e) {
//            log.error("批量添加新闻失败，列表：{}", newsList, e);
//            throw new RuntimeException("Failed to add news in batch", e);
//        }
//        return false;
//    }


    @Override
    public void updateNews(News news) {
        try {
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
        updateNewsStatus(newsId, 3);
    }


    @Override
    public void shelfNews(Long newsId) {
        updateNewsStatus(newsId, 5);
    }

    /**
     *   更改状态
     * @param newsId  新闻id
     * @param statusInt  状态数字
     */
    @Override
    public void  setStatusNews(Long newsId, int statusInt){
        updateNewsStatus(newsId, statusInt);
    }


    /**
     * 获取api的新闻数据
     * @param channel
     * @return
     */
    @Override
    public JSONObject getJisunews(String channel) {
        String host = "https://jisunews.market.alicloudapi.com";
        String path = "/news/get";
        String appcode = "71f6a5c816cb4e33b91615b08b10f392"; // 考虑从配置文件中读取

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "APPCODE " + appcode);
        headers.put("Content-Type", "application/json; charset=UTF-8");

        Map<String, String> querys = new HashMap<>();
        querys.put("channel", channel);
        querys.put("num", "20"); // 获取10条新闻
        querys.put("start", "0"); // 从第一个开始

        String url = host + path;
        url = buildURLWithQueryParams(url, querys);

        try {
            String response = HttpUtils.doGet(url, headers);
            log.info("成功获取数据: {}", response);
            // 解析JSON并处理新闻数据
            JSONObject jsonObject = JSONObject.parseObject(response);

            return  jsonObject;
        } catch (Exception e) {
            log.error("调用Jisu新闻API失败", e);
            throw new RuntimeException("调用Jisu新闻API失败", e);
        }
    }

    private String buildURLWithQueryParams(String url, Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        queryParams.forEach((k, v) -> {
            sb.append(k).append("=").append(v).append("&");
        });
        // 去掉最后一个 "&"
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    @Override
    public List<News> getTop3NewsByViewCount() {
        try {
            QueryWrapper<News> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("viewCount");
            queryWrapper.orderByDesc( "createTime");
            List<News> newsList = newsMapper.selectList(queryWrapper);
            return newsList.stream().limit(3).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to get top 3 news", e);
            throw new RuntimeException("Failed to get top news", e);
        }
    }
}
