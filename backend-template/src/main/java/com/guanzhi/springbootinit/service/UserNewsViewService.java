package com.guanzhi.springbootinit.service;

import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.model.entity.UserNewsView;

import java.util.List;
import java.util.Map;

public interface UserNewsViewService {


    void recordView(Long userId, Long newsId, String newsTitle);

    List<UserNewsView> getUserViewHistory(Long userId, Map<String, Object> params);


    void deleteViewById(Long viewId);
    void deleteAllViewsByUserId(Long userId);
    void deleteAllViews();
}
