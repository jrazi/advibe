package com.tapsell.platform.ad.adstream.ctr

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.PropertySource

@PropertySource("classpath:application.yaml")
@ConfigurationProperties(prefix = "ad-interaction.publishing")
open class AdInteractionPublishingProperties (
    val eventDispatchScheduleRunningIntervalMillis: Int = 100,
    val eventScheduledForDispatchCount: Int = 1
)