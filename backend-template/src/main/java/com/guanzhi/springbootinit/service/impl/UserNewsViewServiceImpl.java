package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.UserNewsViewMapper;
import com.guanzhi.springbootinit.model.entity.UserNewsView;
import com.guanzhi.springbootinit.service.UserNewsViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserNewsViewServiceImpl implements UserNewsViewService {

    @Autowired
    private UserNewsViewMapper userNewsViewMapper;

    @Override
    public void recordView(Long userId, Long newsId,String newsTitle) {

        try {
            QueryWrapper<UserNewsView> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            queryWrapper.eq("newsId", newsId);
            UserNewsView viewRecord = userNewsViewMapper.selectOne(queryWrapper);

            if (viewRecord == null) {
                viewRecord = new UserNewsView();
                viewRecord.setUserId(userId);
                viewRecord.setNewsId(newsId);
                viewRecord.setNewsTitle(newsTitle);
                viewRecord.setViewTime(LocalDateTime.now());
                userNewsViewMapper.insert(viewRecord);
            } else {
                viewRecord.setNewsTitle(newsTitle);
                viewRecord.setViewTime(LocalDateTime.now());
                userNewsViewMapper.updateById(viewRecord);
            }
        } catch (Exception e) {
            log.error("记录浏览失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "记录浏览失败");
        }


    }

    @Override
    public List<UserNewsView> getUserViewHistory(Long userId, Map<String, Object> params) {
        try {
            QueryWrapper<UserNewsView> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            queryWrapper.orderByDesc("viewTime");
            return userNewsViewMapper.selectList(queryWrapper);
        } catch (Exception e) {
            log.error("获取浏览历史失败", e);
            throw new RuntimeException("Failed to get view history", e);
        }
    }

    @Override
    public void deleteViewById(Long viewId) {
        try {
            int result = userNewsViewMapper.deleteById(viewId);
            if (result == 0) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "浏览记录不存在");
            }
        } catch (Exception e) {
            log.error("Failed to delete view by id: {}", viewId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to delete view");
        }
    }

    @Override
    public void deleteAllViewsByUserId(Long userId) {
        try {
            QueryWrapper<UserNewsView> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            int result = userNewsViewMapper.delete(queryWrapper);
            if (result == 0) {
                log.warn("用户 {} 没有浏览记录可以删除", userId);
            } else {
                log.info("成功删除用户 {} 的所有浏览记录，数量: {}", userId, result);
            }
        } catch (Exception e) {
            log.error("Failed to delete all views by user: {}", userId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to delete views");
        }
    }

    @Override
    public void deleteAllViews() {
        try {
            int result = userNewsViewMapper.delete(null);
            log.info("成功删除所有浏览记录，数量: {}", result);
        } catch (Exception e) {
            log.error("Failed to delete all views", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Failed to delete all views");
        }
    }
}