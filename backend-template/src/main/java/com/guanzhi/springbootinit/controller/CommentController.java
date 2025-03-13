package com.guanzhi.springbootinit.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanzhi.springbootinit.common.BaseResponse;
import com.guanzhi.springbootinit.common.ResultUtils;
import com.guanzhi.springbootinit.model.entity.Comment;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping
    public BaseResponse<Boolean> addComment(@RequestBody Comment comment) {
        boolean result = commentService.addComment(comment);
        return ResultUtils.success(result);
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> deleteCommentById(@PathVariable Long id) {
        boolean result = commentService.deleteCommentById(id);
        return ResultUtils.success(result);
        
    }


    @GetMapping("/list")
    public BaseResponse<List<Comment>> getCommentList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long newsId,
            @RequestParam(required = false) String author) {
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("page", page);
        params.put("pageSize", pageSize);
        if (newsId != null) {
            params.put("newsId", newsId);
        }
        if (author != null && !author.isEmpty()) {
            params.put("author", author);
        }
        List<Comment> commentList = commentService.getCommentList(params);
        return  ResultUtils.success(commentList);
    }

    @GetMapping("/{id}")
    public BaseResponse<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        return  ResultUtils.success(comment);
    }
}
