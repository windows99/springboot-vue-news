package com.guanzhi.springbootinit.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.news.NewsQueryRequest;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.service.NewsService;
import com.guanzhi.springbootinit.service.UserNewsViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/news")
@Slf4j
public class NewsController {

    @Autowired
    private NewsService newsService;
    
    @Autowired
    private UserNewsViewService userNewsViewService;



    @PostMapping("/list")
    public BaseResponse<Page<News>> getNewsLists(@RequestBody NewsQueryRequest newsQueryRequest,
                                                 HttpServletRequest request) {
        long current = newsQueryRequest.getCurrent();
        long size = newsQueryRequest.getPageSize();
        Page<News> newsPage = newsService.page(new Page<>(current, size),
                newsService.getQueryWrapper(newsQueryRequest));

        return ResultUtils.success(newsPage);
    }


    @GetMapping("/{id}")
    public BaseResponse<News> getNewsById(@PathVariable Long id, @RequestParam(required = false) Long userId) {
        News news = newsService.getNewsById(id);
        String newsTitle = news.getTitle();
        // 记录浏览
        if (userId  != null) {
            userNewsViewService.recordView(userId, id, newsTitle);
        }
        return ResultUtils.success(news);

    }

    @PostMapping("/add")
    public BaseResponse<String> addNews(@RequestBody News news) {
        newsService.addNews(news);
        return ResultUtils.success("新闻发布成功");

    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNews(
            @PathVariable Long id,
            @RequestBody News news) {
        try {
            news.setId(id);
            newsService.updateNews(news);
            return ResponseEntity.ok(
                    Map.of("message", "News updated successfully", "status", 200));
        } catch (Exception e) {
            log.error("Error updating news with id {}", id, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNewsById(id);
            return ResponseEntity.ok(Map.of("message", "News deleted successfully", "status", 200));
        } catch (Exception e) {
            log.error("Delete news error: ", e);
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage(), "status", 500));
        }
    }

    @PutMapping("/publish/{id}")
    public ResponseEntity<?> publishNews(@PathVariable Long id) {
        try {
            newsService.publishNews(id);
            return ResponseEntity.ok(Map.of("message", "News published successfully", "status", 200));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
    }


    @PutMapping("/shelf/{id}")
    public ResponseEntity<?> shelfNews(@PathVariable Long id) {
        try {
            newsService.shelfNews(id);
            return ResponseEntity.ok(Map.of("message", "News shelved successfully", "status", 200));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
    }


    @PutMapping("/setStatus/{id}")
    public BaseResponse<String> setStatusNews(@PathVariable Long id, @RequestParam int statusInt) {
        newsService.setStatusNews(id, statusInt);
        return ResultUtils.success("更新完成");
    }




    @GetMapping("/jisunews/list")
    public JSONObject getJisunews(String channel){
        JSONObject res = newsService.getJisunews(channel);
        return res;
    }


    @GetMapping("/top3")
    public BaseResponse<List<News>> getTop3NewsByViewCount() {
        List<News> top3News = newsService.getTop3NewsByViewCount();
        return ResultUtils.success(top3News);
    }



}
