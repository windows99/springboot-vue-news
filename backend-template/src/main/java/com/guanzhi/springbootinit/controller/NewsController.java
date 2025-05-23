package com.guanzhi.springbootinit.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.dto.news.NewsQueryRequest;
import com.guanzhi.springbootinit.model.dto.news.NewsAddRequest;
import com.guanzhi.springbootinit.model.dto.news.NewsUpdateRequest;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.service.NewsService;
import com.guanzhi.springbootinit.service.UserNewsViewService;
import com.guanzhi.springbootinit.service.UserService;
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

    @Autowired
    private UserService userService;

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
    public BaseResponse<Long> addNews(@RequestBody NewsAddRequest newsAddRequest, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        // 设置用户ID
        newsAddRequest.setUserId(loginUser.getId());
        Long newsId = newsService.addNews(newsAddRequest);
        return ResultUtils.success(newsId);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateNews(
            @PathVariable Long id,
            @RequestBody NewsUpdateRequest newsUpdateRequest,
            HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(request);
            if (loginUser == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            }
            // 设置用户ID
            newsUpdateRequest.setUserId(loginUser.getId());
            newsUpdateRequest.setId(id);
            boolean result = newsService.updateNews(newsUpdateRequest);
            if (result) {
                return ResponseEntity.ok(
                        Map.of("message", "新闻更新成功", "status", 200));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "新闻更新失败", "status", 400));
            }
        } catch (BusinessException e) {
            log.error("更新新闻失败: ", e);
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage(), "status", 400));
        } catch (Exception e) {
            log.error("更新新闻失败: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(request);
            if (loginUser == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            }
            boolean result = newsService.deleteNewsById(id, loginUser.getId());
            if (result) {
                return ResponseEntity.ok(Map.of("message", "新闻删除成功", "status", 200));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", "新闻删除失败", "status", 400));
            }
        } catch (BusinessException e) {
            log.error("删除新闻失败: ", e);
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "status", 400));
        } catch (Exception e) {
            log.error("删除新闻失败: ", e);
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage(), "status", 500));
        }
    }

    @PutMapping("/publish/{id}")
    public ResponseEntity<?> publishNews(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(request);
            if (loginUser == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            }
            newsService.publishNews(id, loginUser.getId());
            return ResponseEntity.ok(Map.of("message", "新闻发布成功", "status", 200));
        } catch (BusinessException e) {
            log.error("发布新闻失败: ", e);
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "status", 400));
        } catch (Exception e) {
            log.error("发布新闻失败: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
    }

    @PutMapping("/shelf/{id}")
    public ResponseEntity<?> shelfNews(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 获取当前登录用户
            User loginUser = userService.getLoginUser(request);
            if (loginUser == null) {
                throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
            }
            newsService.shelfNews(id, loginUser.getId());
            return ResponseEntity.ok(Map.of("message", "新闻下架成功", "status", 200));
        } catch (BusinessException e) {
            log.error("下架新闻失败: ", e);
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage(), "status", 400));
        } catch (Exception e) {
            log.error("下架新闻失败: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
    }

    @PutMapping("/setStatus/{id}")
    public BaseResponse<String> setStatusNews(@PathVariable Long id, @RequestParam int statusInt, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        newsService.setStatusNews(id, statusInt, loginUser.getId());
        return ResultUtils.success("状态更新完成");
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
