package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.CommentMapper;
import com.guanzhi.springbootinit.model.entity.Comment;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.service.CommentService;
import com.guanzhi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserService userService;

    @Override
    public boolean addComment(Comment comment) {
        try {
            int result = commentMapper.insert(comment);
            return result > 0;
        } catch (Exception e) {
            log.error("Failed to add comment", e);
            throw new RuntimeException("Failed to add comment", e);
        }
    }

    @Override
    public boolean deleteCommentById(Long commentId) {
        try {
            commentMapper.deleteById(commentId);
        } catch (Exception e) {
            log.error("Failed to delete comment by id: {}", commentId, e);
            throw new RuntimeException("Failed to delete comment", e);
        }
        return false;
    }


    @Override
    public List<Comment> getCommentList(Map<String, Object> params) {
        try {
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

            if (params.containsKey("newsId") && params.get("newsId") != null && !String.valueOf(params.get("newsId")).isEmpty()) {
                queryWrapper.eq("newsId", params.get("newsId"));
            }

            if (params.containsKey("userId") && params.get("userId") != null && !String.valueOf(params.get("author")).isEmpty()) {
                queryWrapper.like("userId", String.valueOf(params.get("userId")));
            }

            queryWrapper.eq("isDelete", 0);
            queryWrapper.orderByDesc("createTime");

            Integer currentPage = params.get("page") == null ? 1 : (Integer) params.get("page");
            Integer currentPageSize = params.get("pageSize") == null ? 10 : (Integer) params.get("pageSize");
            Page<Comment> page = new Page<>(currentPage, currentPageSize);
            List<Comment> comments = commentMapper.selectPage(page, queryWrapper).getRecords();

            // 加载并设置用户信息
            for (Comment comment : comments) {
                User user = userService.getById(comment.getUserId());
                if (user != null) {
                    comment.setUsername(user.getUserName());
                    comment.setUserAvatar(user.getUserAvatar());
                }
            }

            return comments;
        } catch (Exception e) {
            log.error("Failed to fetch comment list", e);
            throw new RuntimeException("Failed to fetch comment list", e);
        }
    }

    @Override
    public Comment getCommentById(Long commentId) {

        Comment comment = commentMapper.selectById(commentId);
        User user = userService.getById(comment.getUserId());
        if (user != null) {
            comment.setUsername(user.getUserName());
            comment.setUserAvatar(user.getUserAvatar());
        }
        return comment;
    }

}
