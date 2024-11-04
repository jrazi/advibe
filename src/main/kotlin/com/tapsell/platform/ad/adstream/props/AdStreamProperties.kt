package com.tapsell.platform.ad.adstream.props

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "adstream")
@Validated
data class AdStreamProperties(
    val ctr: CTRProperties,
    val impression: ImpressionProperties
)

data class CTRProperties(
    @field:NotNull
    val mode: String = "static",
    @field:Min(0)
    val mean: Double = 0.1
)

data class ImpressionProperties(
    @field:Min(1000)
    val dispatchInterval: Long = 5000,
    @field:Min(1)
    val eventCountPerDispatch: Int = 2,
    val timeToInterest: TimeToInterestProperties = TimeToInterestProperties()
)

data class TimeToInterestProperties(
    @field:Min(0)
    val mean: Double = 10000.0,
    @field:Min(0)
    val std: Double = 2000.0
)
