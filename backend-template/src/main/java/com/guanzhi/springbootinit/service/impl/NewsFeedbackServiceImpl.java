package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.NewsFeedbackMapper;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsFeedback;
import com.guanzhi.springbootinit.service.NewsFeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 新闻反馈服务实现
 *
 * @author sk
 */
@Slf4j
@Service
public class NewsFeedbackServiceImpl extends ServiceImpl<NewsFeedbackMapper, NewsFeedback> implements NewsFeedbackService {
    @Resource
    private NewsMapper newsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitFeedback(NewsFeedback feedback) {
        // 检查新闻是否存在
        News news = newsMapper.selectById(feedback.getNewsId());
        if (news == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "新闻不存在");
        }

        
        // 保存反馈记录
        boolean result = this.save(feedback);
        
        if (result) {
            // 更新新闻状态为反馈状态
            news.setStatus(6); // 6-反馈新闻（待审核）
            newsMapper.updateById(news);
        }
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewFeedback(Long feedbackId, boolean approved, String reviewNotes, Long reviewerId) {
        NewsFeedback feedback = this.getById(feedbackId);
        if (feedback == null ) {
            return false;
        }

        feedback.setReviewNotes(reviewNotes);
        feedback.setReviewTime(new Date());
        feedback.setReviewerId(reviewerId);
        boolean result = this.updateById(feedback);

        if (result) {
            // 更新新闻状态
            News news = newsMapper.selectById(feedback.getNewsId());
            if (news != null) {
                if (approved) {
                    news.setStatus(2); // 2-审核通过（待发布）
                } else {
                    news.setStatus(4); // 4-审核失败（需要修改）
                }
                newsMapper.updateById(news);
            }
        }

        return result;
    }

    @Override
    public List<NewsFeedback> getPendingFeedbackList() {
        return this.lambdaQuery()
                .eq(NewsFeedback::getIsDelete, 0)
                .orderByDesc(NewsFeedback::getCreateTime)
                .list();
    }
} 