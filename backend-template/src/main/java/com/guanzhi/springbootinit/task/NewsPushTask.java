package com.guanzhi.springbootinit.task;

import com.guanzhi.springbootinit.service.NewsPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class NewsPushTask {

    @Resource
    private NewsPushService newsPushService;

    /**
     * 每天早上8点推送新闻
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void pushNewsDaily() {
        log.info("开始执行每日新闻推送任务");
        int count = newsPushService.pushNewsToAllUsers();
        log.info("每日新闻推送任务完成，成功推送给{}个用户", count);
    }
    
    /**
     * 每天中午12点推送热门新闻
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void pushHotNewsAtNoon() {
        log.info("开始执行中午热门新闻推送任务");
        int count = newsPushService.pushNewsToAllUsers();
        log.info("中午热门新闻推送任务完成，成功推送给{}个用户", count);
    }
    
    /**
     * 每天晚上8点推送新闻
     */
    @Scheduled(cron = "0 0 20 * * ?")
    public void pushNewsAtNight() {
        log.info("开始执行晚间新闻推送任务");
        int count = newsPushService.pushNewsToAllUsers();
        log.info("晚间新闻推送任务完成，成功推送给{}个用户", count);
    }
} 