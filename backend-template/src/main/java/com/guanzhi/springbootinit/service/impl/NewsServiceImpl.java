package com.guanzhi.springbootinit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.guanzhi.springbootinit.model.dto.news.NewsAddRequest;
import com.guanzhi.springbootinit.model.dto.news.NewsUpdateRequest;
import com.guanzhi.springbootinit.model.entity.UserOperationLog;
import com.guanzhi.springbootinit.service.SensitiveWordService;
import com.guanzhi.springbootinit.service.UserOperationLogService;
import com.guanzhi.springbootinit.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.news.NewsQueryRequest;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.service.NewsService;
import com.obs.services.internal.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Autowired
    private UserOperationLogService userOperationLogService;

    @Override
    public QueryWrapper<News> getQueryWrapper(NewsQueryRequest newsQueryRequest) {
        QueryWrapper<News> queryWrapper = new QueryWrapper<>();
        if (newsQueryRequest == null) {
            return queryWrapper;
        }
        
        // 添加新闻ID筛选条件
        Long id = newsQueryRequest.getId();
        if (id != null) {
            queryWrapper.eq("id", id);
        }
        
        // 添加标题筛选条件
        String title = newsQueryRequest.getTitle();
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like("title", title);
        }
        
        // 添加内容筛选条件
        String content = newsQueryRequest.getContent();
        if (StringUtils.isNotBlank(content)) {
            queryWrapper.like("content", content);
        }
        
        // 添加分类筛选条件
        Long category = newsQueryRequest.getCategory();
        if (category != null) {
            queryWrapper.eq("category", category);
        }
        
        // 添加状态筛选条件
        Integer status = newsQueryRequest.getStatus();
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        // 添加作者筛选条件
        String author = newsQueryRequest.getAuthor();
        if (StringUtils.isNotBlank(author)) {
            queryWrapper.like("author", author);
        }
        
        // 添加来源筛选条件
        String source = newsQueryRequest.getSource();
        if (StringUtils.isNotBlank(source)) {
            queryWrapper.like("source", source);
        }
        
        // 只查询未删除的新闻
        queryWrapper.eq("isDelete", 0);
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc("createTime");
        
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
    public Long addNews(NewsAddRequest newsAddRequest) {
        // 参数校验
        if (newsAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        if (StringUtils.isBlank(newsAddRequest.getTitle())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻标题不能为空");
        }
        if (StringUtils.isBlank(newsAddRequest.getContent())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻内容不能为空");
        }
        if (newsAddRequest.getCategory() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻分类不能为空");
        }
        if (StringUtils.isBlank(newsAddRequest.getAuthor())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻作者不能为空");
        }
        if (StringUtils.isBlank(newsAddRequest.getSource())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "新闻来源不能为空");
        }
        
        // 检查标题和内容是否包含敏感词
        if (sensitiveWordService.checkContentForSensitive(newsAddRequest.getTitle())) {
            throw new BusinessException(ErrorCode.SENSITIVE_WORDS_FOUND, "标题包含敏感词");
        }
        if (sensitiveWordService.checkContentForSensitive(newsAddRequest.getContent())) {
            throw new BusinessException(ErrorCode.SENSITIVE_WORDS_FOUND, "内容包含敏感词");
        }
        
        // 过滤敏感词
        String filteredTitle = sensitiveWordService.filterSensitiveWords(newsAddRequest.getTitle());
        String filteredContent = sensitiveWordService.filterSensitiveWords(newsAddRequest.getContent());
        
        // 创建新闻对象
        News news = new News();
        news.setTitle(filteredTitle);
        news.setContent(filteredContent);
        news.setCategory(newsAddRequest.getCategory());
        news.setStatus(newsAddRequest.getStatus() != null ? newsAddRequest.getStatus() : 1); // 默认状态为1（草稿）
        news.setAuthor(newsAddRequest.getAuthor());
        news.setSource(newsAddRequest.getSource());
        news.setCoverImage(newsAddRequest.getCoverImage());
        news.setCreatetime(new Date());
        news.setUpdateTime(new Date());
        news.setIsdelete(0);
        news.setViewcount(0);
        news.setLikeCount(0);
        news.setCommentCount(0);
        
        try {
            // 保存新闻
            int result = newsMapper.insert(news);
            if (result <= 0) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存新闻失败");
            }
            
            // 记录操作日志
            try {
                UserOperationLog log = new UserOperationLog();
                log.setUserId(newsAddRequest.getUserId() != null ? newsAddRequest.getUserId() : 1L); // 如果用户ID为空，使用系统默认ID
                log.setOperationType("NEWS_ADD"); // 使用更具体的操作类型
                log.setTargetId(news.getId());
                log.setTargetType("NEWS");
                log.setOperationTime(new Date());
                log.setOperationDetail("发布新闻：" + news.getTitle());
                log.setCreateTime(new Date());
                log.setUpdateTime(new Date());
                log.setIsDelete(0);
                userOperationLogService.save(log);
            } catch (Exception e) {
                // 日志记录失败不影响主流程
                log.error("记录操作日志失败", e);
            }
            
            return news.getId();
        } catch (Exception e) {
            log.error("发布新闻失败:{}", newsAddRequest, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发布新闻失败：" + e.getMessage());
        }
    }

    @Override
    public boolean deleteNewsById(Long id, Long userId) {
        try {
            // 删除新闻
            int result = newsMapper.deleteById(id);
            if (result > 0) {
                // 记录操作日志
                UserOperationLog log = new UserOperationLog();
                log.setUserId(userId);
                log.setOperationType("DELETE");
                log.setTargetId(id);
                log.setTargetType("NEWS");
                log.setOperationTime(new Date());
                log.setOperationDetail("删除新闻");
                log.setCreateTime(new Date());
                log.setUpdateTime(new Date());
                log.setIsDelete(0);
                userOperationLogService.save(log);
            }
            return result > 0;
        } catch (Exception e) {
            log.error("删除新闻失败: ", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除新闻失败：" + e.getMessage());
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
    public void publishNews(Long newsId, Long userId) {
        try {
            News news = newsMapper.selectById(newsId);
            if (news == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "新闻不存在");
            }
            news.setStatus(3); // 3-已发布
            news.setUpdateTime(new Date());
            int result = newsMapper.updateById(news);
            if (result > 0) {
                // 记录操作日志
                UserOperationLog log = new UserOperationLog();
                log.setUserId(userId);
                log.setOperationType("PUBLISH");
                log.setTargetId(newsId);
                log.setTargetType("NEWS");
                log.setOperationTime(new Date());
                log.setOperationDetail("发布新闻：" + news.getTitle());
                log.setCreateTime(new Date());
                log.setUpdateTime(new Date());
                log.setIsDelete(0);
                userOperationLogService.save(log);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "发布新闻失败");
            }
        } catch (Exception e) {
            log.error("发布新闻失败: ", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发布新闻失败：" + e.getMessage());
        }
    }

    @Override
    public void shelfNews(Long newsId, Long userId) {
        try {
            News news = newsMapper.selectById(newsId);
            if (news == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "新闻不存在");
            }
            news.setStatus(5); // 5-已下架
            news.setUpdateTime(new Date());
            int result = newsMapper.updateById(news);
            if (result > 0) {
                // 记录操作日志
                UserOperationLog log = new UserOperationLog();
                log.setUserId(userId);
                log.setOperationType("SHELF");
                log.setTargetId(newsId);
                log.setTargetType("NEWS");
                log.setOperationTime(new Date());
                log.setOperationDetail("下架新闻：" + news.getTitle());
                log.setCreateTime(new Date());
                log.setUpdateTime(new Date());
                log.setIsDelete(0);
                userOperationLogService.save(log);
            } else {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "下架新闻失败");
            }
        } catch (Exception e) {
            log.error("下架新闻失败: ", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "下架新闻失败：" + e.getMessage());
        }
    }

    /**
     *   更改状态
     * @param newsId  新闻id
     * @param statusInt  状态数字
     */
    @Override
    public void  setStatusNews(Long newsId, int statusInt, Long userId){
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
        String method = "GET";
        String appcode = "a6c46eb8c000420b93c04b549652b1bb";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("channel", "头条");
        querys.put("num", "10");
        querys.put("start", "0");


        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            String responseBody = EntityUtils.toString(response.getEntity());
            return JSONObject.parseObject(responseBody);
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

    @Override
    public boolean updateNews(NewsUpdateRequest newsUpdateRequest) {
        if (newsUpdateRequest == null || newsUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        
        // 检查标题和内容是否包含敏感词
        if (sensitiveWordService.checkContentForSensitive(newsUpdateRequest.getTitle())) {
            throw new BusinessException(ErrorCode.SENSITIVE_WORDS_FOUND, "标题包含敏感词");
        }
        if (sensitiveWordService.checkContentForSensitive(newsUpdateRequest.getContent())) {
            throw new BusinessException(ErrorCode.SENSITIVE_WORDS_FOUND, "内容包含敏感词");
        }
        
        // 过滤敏感词
        String filteredTitle = sensitiveWordService.filterSensitiveWords(newsUpdateRequest.getTitle());
        String filteredContent = sensitiveWordService.filterSensitiveWords(newsUpdateRequest.getContent());
        
        // 创建新闻对象
        News news = new News();
        news.setId(newsUpdateRequest.getId());
        news.setTitle(filteredTitle);
        news.setContent(filteredContent);
        news.setCategory(newsUpdateRequest.getCategory());
        news.setStatus(newsUpdateRequest.getStatus());
        news.setAuthor(newsUpdateRequest.getAuthor());
        news.setSource(newsUpdateRequest.getSource());
        news.setCoverImage(newsUpdateRequest.getCoverImage());
        news.setUpdateTime(new Date());
        
        try {
            // 更新新闻
            boolean result = newsMapper.updateById(news) > 0;
            
            if (result) {
                // 记录操作日志
                UserOperationLog log = new UserOperationLog();
                log.setUserId(newsUpdateRequest.getUserId());
                log.setOperationType("UPDATE");
                log.setTargetId(newsUpdateRequest.getId());
                log.setTargetType("NEWS");
                log.setOperationTime(new Date());
                log.setOperationDetail("更新新闻：" + newsUpdateRequest.getTitle());
                log.setCreateTime(new Date());
                log.setUpdateTime(new Date());
                log.setIsDelete(0);
                userOperationLogService.save(log);
            }
            
            return result;
        } catch (Exception e) {
            log.error("更新新闻失败:{}", newsUpdateRequest);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新新闻失败");
        }
    }
}
