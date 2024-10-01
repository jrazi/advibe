package com.tapsell.platform.ad.adtrack.model

import com.tapsell.platform.ad.contract.validation.ValidTimestamp
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.cassandra.core.mapping.Column

@Table("ad_event")
data class AdEvent(

    @PrimaryKey
    @field:NotBlank(message = "Request ID cannot be null or blank")
    @field:Size(min = 1, max = 100, message = "Request ID must be between 1 and 100 characters")
    val requestId: String,

    @field:Size(min = 1, max = 50, message = "Ad ID must be between 1 and 50 characters")
    val adId: String? = null,

    @field:Size(min = 1, max = 255, message = "Ad title must be between 1 and 255 characters")
    val adTitle: String? = null,

    @field:PositiveOrZero(message = "Advertiser cost must be zero or a positive value")
    val advertiserCost: Double? = null,

    @field:Size(min = 1, max = 50, message = "App ID must be between 1 and 50 characters")
    val appId: String? = null,

    @field:Size(min = 1, max = 255, message = "App title must be between 1 and 255 characters")
    val appTitle: String? = null,

    @field:ValidTimestamp
    val impressionTime: Long? = null,

    @field:ValidTimestamp
    val clickTime: Long? = null
)
