package com.guanzhi.springbootinit.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ResultUtils;
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
    public ResponseEntity<?> addNews(@RequestBody News news) {
        try {
            boolean result = newsService.addNews(news);
            if (result) {
                return ResponseEntity.ok()
                        .body(Map.of("message", "News added successfully!", "status", 200));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Failed to add news.", "status", 400));
            }
        } catch (Exception e) {
            log.error("Error adding news: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
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




    @GetMapping("/jisunews/list")
    public JSONObject getJisunews(String channel){
        JSONObject res = newsService.getJisunews(channel);
        return res;
    }

//    @PostMapping("/batch")
//    public BaseResponse<?> batchAddNews(
//            @RequestBody List<News> newsList) {
//       boolean res =  newsService.batchAddNews(newsList);
//
//        return ResultUtils.success(res);
//    }


}
