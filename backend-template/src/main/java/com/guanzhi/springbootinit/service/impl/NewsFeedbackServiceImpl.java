package com.guanzhi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanzhi.springbootinit.common.ErrorCode;
import com.guanzhi.springbootinit.exception.BusinessException;
import com.guanzhi.springbootinit.mapper.NewsFeedbackMapper;
import com.guanzhi.springbootinit.mapper.NewsMapper;
import com.guanzhi.springbootinit.mapper.UserMapper;
import com.guanzhi.springbootinit.model.entity.News;
import com.guanzhi.springbootinit.model.entity.NewsFeedback;
import com.guanzhi.springbootinit.model.entity.User;
import com.guanzhi.springbootinit.model.entity.UserOperationLog;
import com.guanzhi.springbootinit.model.vo.NewsFeedbackVO;
import com.guanzhi.springbootinit.service.NewsFeedbackService;
import com.guanzhi.springbootinit.service.NewsService;
import com.guanzhi.springbootinit.service.UserOperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

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
    
    @Resource
    private UserMapper userMapper;

    @Autowired
    private NewsService newsService;

    @Autowired
    private UserOperationLogService userOperationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean submitFeedback(NewsFeedback feedback) {
        // 检查新闻是否存在
        News news = newsMapper.selectById(feedback.getNewsId());
        if (news == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "新闻不存在");
        }

        log.info("开始处理新闻反馈，新闻ID：{}，当前状态：{}", feedback.getNewsId(), news.getStatus());
        
        // 保存反馈记录
        boolean result = this.save(feedback);
        
        if (result) {
            // 更新新闻状态为反馈状态
            news.setStatus(6); // 6-反馈新闻（待审核）
            int updateResult = newsMapper.updateById(news);
            log.info("更新新闻状态结果：{}", updateResult > 0 ? "成功" : "失败");
            
            if (updateResult <= 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新新闻状态失败");
            }

            // 记录操作日志
            UserOperationLog log = new UserOperationLog();
            log.setUserId(feedback.getUserId());
            log.setOperationType("FEEDBACK");
            log.setTargetId(feedback.getNewsId());
            log.setTargetType("NEWS");
            log.setOperationTime(new Date());
            log.setOperationDetail("提交新闻反馈：" + feedback.getContent());
            log.setCreateTime(new Date());
            log.setUpdateTime(new Date());
            log.setIsDelete(0);
            userOperationLogService.save(log);
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

            // 记录操作日志
            UserOperationLog log = new UserOperationLog();
            log.setUserId(reviewerId);
            log.setOperationType("PROCESS_FEEDBACK");
            log.setTargetId(feedbackId);
            log.setTargetType("FEEDBACK");
            log.setOperationTime(new Date());
            log.setOperationDetail("处理新闻反馈：" + (approved ? "审核通过" : "审核失败"));
            log.setCreateTime(new Date());
            log.setUpdateTime(new Date());
            log.setIsDelete(0);
            userOperationLogService.save(log);
        }

        return result;
    }

    @Override
    public Page<NewsFeedbackVO> getPendingFeedbackList(long current, long pageSize) {
        // 1. 分页查询反馈列表
        Page<NewsFeedback> feedbackPage = this.lambdaQuery()
                .eq(NewsFeedback::getIsDelete, 0)
                .orderByDesc(NewsFeedback::getCreateTime)
                .page(new Page<>(current, pageSize));
        
        // 2. 获取所有新闻ID和用户ID
        List<Long> newsIds = feedbackPage.getRecords().stream()
                .map(NewsFeedback::getNewsId)
                .collect(Collectors.toList());
        List<Long> userIds = feedbackPage.getRecords().stream()
                .map(NewsFeedback::getUserId)
                .collect(Collectors.toList());
        
        // 3. 查询新闻信息
        final Map<Long, String> newsTitleMap;
        if (!newsIds.isEmpty()) {
            newsTitleMap = newsMapper.selectBatchIds(newsIds).stream()
                    .collect(Collectors.toMap(News::getId, News::getTitle));
        } else {
            newsTitleMap = new HashMap<>();
        }
        
        // 4. 查询用户信息
        final Map<Long, String> usernameMap;
        if (!userIds.isEmpty()) {
            usernameMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, User::getUserName));
        } else {
            usernameMap = new HashMap<>();
        }
        
        // 5. 转换为VO对象
        Page<NewsFeedbackVO> voPage = new Page<>(current, pageSize, feedbackPage.getTotal());
        List<NewsFeedbackVO> voList = feedbackPage.getRecords().stream().map(feedback -> {
            NewsFeedbackVO vo = new NewsFeedbackVO();
            BeanUtils.copyProperties(feedback, vo);
            vo.setNewsTitle(newsTitleMap.get(feedback.getNewsId()));
            vo.setUsername(usernameMap.get(feedback.getUserId()));
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
} 