package com.guanzhi.springbootinit.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanzhi.springbootinit.model.entity.NewsFeedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 新闻反馈 Mapper
 *
 * @author sk
 */
@Mapper
public interface NewsFeedbackMapper extends BaseMapper<NewsFeedback> {
} 