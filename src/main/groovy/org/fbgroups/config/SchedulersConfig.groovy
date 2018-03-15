package org.fbgroups.config

import org.fbgroups.entity.UserProfileRepository
import org.fbgroups.services.IStartDownloads
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.config.ScheduledTaskRegistrar

/**
 * Created by Pavel on 9/29/2015.
 */
@Configuration
@EnableScheduling
class SchedulersConfig implements SchedulingConfigurer {

    private static final long PERIOD = 60 * 1*  1000L

    @Autowired
    UserProfileRepository repo

    @Autowired
    ApplicationContext factory


    @Autowired
    IStartDownloads startDownloads


    @Bean
    ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);
        scheduler.setThreadNamePrefix("task-")
        scheduler.setAwaitTerminationSeconds(60)
        scheduler.setWaitForTasksToCompleteOnShutdown(true);        return scheduler;
    }

    @Override
    void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addFixedRateTask(startDownloads, PERIOD)
    }
}
