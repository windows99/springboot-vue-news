package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.mapper.CommentMapper;
import com.guanzhi.springbootinit.model.entity.Comment;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.service.CommentService;
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
    public void deleteCommentById(Long commentId) {
        try {
            commentMapper.deleteById(commentId);
        } catch (Exception e) {
            log.error("Failed to delete comment by id: {}", commentId, e);
            throw new RuntimeException("Failed to delete comment", e);
        }
    }


    @Override
    public List<Comment> getCommentList(Map<String, Object> params) {
        try {
            // 使用QueryWrapper构造查询条件
            QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();


            // 根据newsId参数添加LIKE条件
            if (params.containsKey("newsId") && params.get("newsId") != null && !String.valueOf(params.get("newsId")).isEmpty()) {
                queryWrapper.like("newsId", String.valueOf(params.get("newsId")));
            }
            // 根据author参数添加LIKE条件
            if (params.containsKey("author") && params.get("author") != null && !String.valueOf(params.get("author")).isEmpty()) {
                queryWrapper.like("author", String.valueOf(params.get("author")));
            }
            // 确保只查询未删除的记录
            queryWrapper.eq("isDelete", 0);
            // 增加排序条件，按创建时间降序排列
            queryWrapper.orderByDesc("createTime");

//            // 创建Page对象，设置分页参数
            Integer currentPage = params.get("page") == null ? 1 : (Integer) params.get("page");
            Integer currentPageSize = params.get("pageSize") == null ? 10 : (Integer) params.get("pageSize");
            Page<Comment> page = new Page<>(currentPage, currentPageSize);

            return commentMapper.selectPage(page, queryWrapper).getRecords();
        } catch (Exception e) {
            log.error("Failed to fetch comment list", e);
            throw new RuntimeException("Failed to fetch comment list", e);
        }
    }

    @Override
    public Comment getCommentById(Long commentId) {
        try {
            return commentMapper.selectById(commentId);
        } catch (Exception e) {
            log.error("Failed to fetch comment by id: {}", commentId, e);
            throw new RuntimeException("Failed to fetch comment", e);
        }
    }

}
