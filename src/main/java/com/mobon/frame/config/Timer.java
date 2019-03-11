package com.mobon.frame.config;

import com.mobon.frame.service.FrameCycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class Timer {

    private AtomicInteger loopCount = new AtomicInteger();

    @Autowired
    private StopWatch watch;

    @Autowired
    private FrameCycleService frameCycleService;

    @Value("${spring.task.name}")
    private String taskNamePrefix;

    @Value("${spring.timerName}")
    private String timerName;

    @PostConstruct
    public void init() {
        log.info("{} init", timerName);
        watch.start();
    }

    @Scheduled(fixedDelayString = "${spring.task.fixedDelay}")
    public void tick() throws InterruptedException {
        watch.stop();
        String taskName = taskNamePrefix + "-" + loopCount.getAndIncrement();
        log.info("{} start!!", taskName);
        /**
         * 1. 그룹핑으로 나눠서 배치처리 (100개씩)
         * saveFrameCycleRankPdctAllProcess
         *
         * 2. 처음부터 그룹핑한 전체 데이터로 가져와서 랍다 그룹핑으로 분리하고 정리해서 처리
         * frameCycleRankPdctProcess2
         *
         * 3. insert select 쿼리(한방쿼리)로 처리
         * saveFrameCycleRankPdctAllProcess
         *
         */
        //frameCycleService.frameCycleStatsPdctScheduler();
        //frameCycleService.frameCycleRankPdctProcess2();
        int result = frameCycleService.saveFrameCycleRankPdctAllProcess();
        log.info("result - {} !!", result);

        log.info("{} end!!", taskName);
        watch.start(taskName);
    }

    @Bean
    public StopWatch watch() {
        return new StopWatch();
    }
}
