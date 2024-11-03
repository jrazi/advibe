package com.tapsell.platform.ad.adstream.ctr

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource


@PropertySource("classpath:application.yaml")
@ConfigurationProperties(prefix = "ad-interaction.modeling")
open class AdInteractionModelingProperties(
    val clickThroughRate: Int = 0,
    val meanClickTime: Int = 100,
    val stdClickTime: Int = 0,
    val clickTimeShape: Double = 1.0,
    val clickTimeScale: Double = 1.0,
    val impressionArrivalRatio: Double = 1.0,
    val clickArrivalRatio: Double = 1.0
)
