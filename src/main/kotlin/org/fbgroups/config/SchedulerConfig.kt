package org.fbgroups.config

import org.fbgroups.services.IDuplicatesSearcher
import org.fbgroups.services.ILikesReporter
import org.springframework.beans.factory.annotation.Autowired
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
class SchedulerConfig : SchedulingConfigurer {


    @Autowired
    lateinit var duplicatesSearcher: IDuplicatesSearcher

    @Autowired
    lateinit var likesReporter: ILikesReporter


    @Bean
    fun taskScheduler(): ThreadPoolTaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 20
        scheduler.threadNamePrefix = "task-"
        scheduler.setAwaitTerminationSeconds(60)
        scheduler.setWaitForTasksToCompleteOnShutdown(true)
        return scheduler
    }

    override fun configureTasks(taskRegistrar: ScheduledTaskRegistrar) {
        taskRegistrar.addFixedRateTask(duplicatesSearcher, DUPLICATES_PERIOD)
        taskRegistrar.addFixedRateTask(likesReporter, LIKES_PERIOD)
    }

    companion object {

        private const val DUPLICATES_PERIOD = 60 * 60 * 1000L
        private const val LIKES_PERIOD = 60 * 60 * 1000L
    }
}
