package com.guanzhi.springbootinit.job;

import com.guanzhi.springbootinit.service.NewsPushConfigService;
import com.guanzhi.springbootinit.service.NewsPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 新闻推送定时任务调度器
 */
@Component
@Slf4j
public class NewsPushScheduler {
    
    @Resource
    private NewsPushConfigService newsPushConfigService;
    
    @Resource
    private NewsPushService newsPushService;
    
    /**
     * 每分钟检查一次需要执行的定时任务
     */
    @Scheduled(cron = "0 * * * * ?")
    public void schedulePushCheck() {
        log.info("开始执行定时推送检查...");
        try {
            int tasksExecuted = newsPushConfigService.executeScheduledPushTasks();
            log.info("成功执行了 {} 个定时推送任务", tasksExecuted);
        } catch (Exception e) {
            log.error("执行定时推送检查失败", e);
        }
    }
    
    /**
     * 每5分钟检查一次待推送的记录
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void pendingPushCheck() {
        log.info("开始检查待推送记录...");
        try {
            int tasksExecuted = newsPushService.executePendingPushes();
            if (tasksExecuted > 0) {
                log.info("成功执行了 {} 个待推送记录", tasksExecuted);
            }
        } catch (Exception e) {
            log.error("检查待推送记录失败", e);
        }
    }
    
    /**
     * 每天凌晨3点执行全量用户推送
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void dailyPushForAllUsers() {
        log.info("开始执行每日推送...");
        try {
            int userCount = newsPushService.pushNewsToAllUsers();
            log.info("成功向 {} 个用户推送了每日新闻", userCount);
        } catch (Exception e) {
            log.error("执行每日推送失败", e);
        }
    }
} 