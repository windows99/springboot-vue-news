package com.guanzhi.springbootinit.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guanzhi.springbootinit.mapper.NewsPushMapper;
import com.guanzhi.springbootinit.model.entity.NewsPush;
import com.guanzhi.springbootinit.service.NewsPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class NewsPushTask {

    @Resource
    private NewsPushService newsPushService;
    
    @Resource
    private NewsPushMapper newsPushMapper;

    /**
     * 每天早上8点推送新闻
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void pushNewsAt8AM() {
        log.info("开始执行早上8点新闻推送任务");
        int count = newsPushService.pushNewsToAllUsers();
        log.info("早上8点新闻推送任务完成，成功推送给 {} 个用户", count);
    }

    /**
     * 每天中午12点推送新闻
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void pushNewsAt12PM() {
        log.info("开始执行中午12点新闻推送任务");
        int count = newsPushService.pushNewsToAllUsers();
        log.info("中午12点新闻推送任务完成，成功推送给 {} 个用户", count);
    }

    /**
     * 每天晚上8点推送新闻
     */
    @Scheduled(cron = "0 0 20 * * ?")
    public void pushNewsAt8PM() {
        log.info("开始执行晚上8点新闻推送任务");
        int count = newsPushService.pushNewsToAllUsers();
        log.info("晚上8点新闻推送任务完成，成功推送给 {} 个用户", count);
    }
    
    /**
     * 每分钟检查一次待推送的新闻
     */
    @Scheduled(cron = "0 * * * * ?")
    public void checkScheduledPushes() {
        log.info("开始检查待推送的新闻");
        
        // 查询所有待推送的新闻
        LambdaQueryWrapper<NewsPush> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NewsPush::getStatus, 0) // 0-待推送
                .eq(NewsPush::getIsDelete, 0)
                .le(NewsPush::getPushTime, new Date()); // 推送时间已到
        
        List<NewsPush> pushes = newsPushMapper.selectList(queryWrapper);
        
        if (pushes.isEmpty()) {
            return;
        }
        
        log.info("发现 {} 条待推送的新闻", pushes.size());
        
        // 按用户ID分组推送
        for (NewsPush push : pushes) {
            try {
                // 更新推送状态为已推送
                push.setStatus(1); // 1-已推送
                push.setUpdateTime(new Date());
                newsPushMapper.updateById(push);
                
                // 执行推送
                newsPushService.pushSpecificNewsToUser(push.getUserId(), push.getNewsId());
                
                log.info("成功推送新闻 {} 给用户 {}", push.getNewsId(), push.getUserId());
            } catch (Exception e) {
                log.error("推送新闻 {} 给用户 {} 失败: {}", push.getNewsId(), push.getUserId(), e.getMessage());
            }
        }
        
        log.info("待推送新闻处理完成");
    }
} 