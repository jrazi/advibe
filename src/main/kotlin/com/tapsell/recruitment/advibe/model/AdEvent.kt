package com.tapsell.recruitment.advibe.model

import com.tapsell.recruitment.advibe.util.validation.ValidTimestamp
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

    @Column("ad_id")
    @field:Size(min = 1, max = 50, message = "Ad ID must be between 1 and 50 characters")
    val adId: String? = null,

    @Column("ad_title")
    @field:Size(min = 1, max = 255, message = "Ad title must be between 1 and 255 characters")
    val adTitle: String? = null,

    @Column("advertiser_cost")
    @field:PositiveOrZero(message = "Advertiser cost must be zero or a positive value")
    val advertiserCost: Double? = null,

    @Column("app_id")
    @field:Size(min = 1, max = 50, message = "App ID must be between 1 and 50 characters")
    val appId: String? = null,

    @Column("app_title")
    @field:Size(min = 1, max = 255, message = "App title must be between 1 and 255 characters")
    val appTitle: String? = null,

    @Column("impression_time")
    @field:ValidTimestamp
    val impressionTime: Long? = null,

    @Column("click_time")
    @field:ValidTimestamp
    val clickTime: Long? = null
)
