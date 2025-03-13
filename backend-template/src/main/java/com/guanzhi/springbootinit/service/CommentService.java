package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.entity.Comment;

import java.util.List;
import java.util.Map;


public interface CommentService extends IService<Comment> {

    boolean addComment(Comment comment);

    boolean deleteCommentById(Long commentId);

    List<Comment> getCommentList(Map<String, Object> params);

    Comment getCommentById(Long commentId);

}
