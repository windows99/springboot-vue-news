package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.constant.CommonConstant;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.NewTagMapper;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.model.dto.news.NewsTagQueryRequset;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsTag;
import com.guanzhi.springbootinit.service.NewsService;
import com.guanzhi.springbootinit.service.NewsTagService;
import com.guanzhi.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class NewsTagServiceImpl extends ServiceImpl<NewTagMapper, NewsTag> implements NewsTagService {


    @Autowired
    private NewTagMapper newTagMapper;

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public boolean addTag(NewsTag newsTag) {
        try {
            int result = newTagMapper.insert(newsTag);
            return result > 0;
        } catch (Exception e) {
            log.error("Failed to add news tag", e);
            throw new RuntimeException("Failed to add news tag", e);
        }
    }

    @Override
    public boolean deleteTagById(Long tagId) {
        try {
            int result = newTagMapper.deleteById(tagId);
            if (result == 0) {
                throw new RuntimeException("Failed to delete news tag with id: " + tagId);
            }
        } catch (Exception e) {
            log.error("Delete news tag error: ", e);
            throw new RuntimeException(e);
        }
        return false;
    }

//    private boolean isTagInUse(Long tagId) {
//        try {
//            News news = new News();
//            news.createCriteria().andTagIdsLike("%," + tagId + ",%");
//            int count = newsMapper.countByExample(example);
//            return count > 0;
//        } catch (Exception e) {
//            log.error("检查标签ID {} 是否在使用中失败", tagId, e);
//            return true; // 默认视为被使用，防止误删
//        }
//    }


    @Override
    public boolean updateTag(NewsTag newsTag) {
        try {
            newTagMapper.updateById(newsTag);
        } catch (Exception e) {
            log.error("Failed to update news tag: {}");
            throw new RuntimeException("Failed to update news tag", e);
        }
        return false;
    }

    @Override
    public NewsTag getTagById(Long tagId) {
        try {
            return newTagMapper.selectById(tagId);
        } catch (Exception e) {
            log.error("Failed to fetch news tag by id: {}");
            throw new RuntimeException("Failed to fetch news tag", e);
        }
    }

    @Override
    public List<NewsTag> getTagList(Map<String, Object> params) {
        try {
            return newTagMapper.selectList(null);
        } catch (Exception e) {
            log.error("Failed to fetch news tag list", e);
            throw new RuntimeException("Failed to fetch news tag list", e);
        }
    }


    @Override
    public QueryWrapper<NewsTag> getQueryWrapper(NewsTagQueryRequset request) {

        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        System.out.println(request);
        QueryWrapper<NewsTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("isDelete", 0)
                .orderBy(SqlUtils.validSortField(request.getSortField()),
                        request.getSortOrder().equals(CommonConstant.SORT_ORDER_ASC),
                        StringUtils.isBlank(request.getSortField()) ? "createTime" : request.getSortField());
        System.out.println(queryWrapper);
        return queryWrapper;
    }
}
