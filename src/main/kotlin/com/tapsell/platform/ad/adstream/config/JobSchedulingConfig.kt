package com.tapsell.platform.ad.adstream.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler

@EnableScheduling
@EnableAsync
@Configuration
open class EventSchedulingConfig {

    @Bean
    open fun taskScheduler(): TaskScheduler {
        val scheduler = ThreadPoolTaskScheduler()
        scheduler.poolSize = 15
        scheduler.setThreadNamePrefix("ad-event-scheduler-")
        scheduler.initialize()
        return scheduler
    }

}