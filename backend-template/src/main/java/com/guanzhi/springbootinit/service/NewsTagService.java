package com.guanzhi.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guanzhi.springbootinit.model.dto.news.NewsTagQueryRequset;
import com.guanzhi.springbootinit.model.entity.NewsTag;

import java.util.List;
import java.util.Map;

public interface NewsTagService extends IService<NewsTag> {

    boolean addTag(NewsTag newsTag);

    boolean deleteTagById(Long tagId);

    boolean updateTag(NewsTag newsTag);

    NewsTag getTagById(Long tagId);

    List<NewsTag> getTagList(Map<String, Object> params);

    QueryWrapper<NewsTag> getQueryWrapper(NewsTagQueryRequset newsTagQueryRequset);
}
