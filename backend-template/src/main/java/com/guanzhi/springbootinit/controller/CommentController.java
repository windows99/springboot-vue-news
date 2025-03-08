package com.guanzhi.springbootinit.controller;


import com.guanzhi.springbootinit.model.entity.Comment;
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
    public ResponseEntity<?> addComment(@RequestBody Comment comment) {
        try {
            boolean result = commentService.addComment(comment);
            if (result) {
                return ResponseEntity.ok(Map.of("message", "Comment added successfully", "status", 200));
            } else {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Failed to add comment", "status", 400));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommentById(@PathVariable Long id) {
        try {
            commentService.deleteCommentById(id);
            return ResponseEntity.ok(Map.of("message", "Comment deleted successfully", "status", 200));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", e.getMessage(), "status", 500));
        }
    }


    @GetMapping("/list")
    public ResponseEntity<?> getCommentList(
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
        try {
            List<Comment> commentList = commentService.getCommentList(params);
            return ResponseEntity.ok(commentList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to fetch comment list", "status", 500));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            Comment comment = commentService.getCommentById(id);
            if (comment != null) {
                return ResponseEntity.ok(comment);
            } else {
                return ResponseEntity.notFound()
                        .build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Failed to fetch comment", "status", 500));
        }
    }
}
