package com.tapsell.platform.ad.contract.dto

import com.tapsell.platform.ad.contract.validation.ValidTimestamp
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero

data class ImpressionEventDto(
    @field:NotBlank(message = "Request ID cannot be blank")
    override val requestId: String = "",

    @field:NotBlank(message = "Ad ID cannot be blank")
    val adId: String = "",

    @field:NotBlank(message = "Ad title cannot be blank")
    val adTitle: String = "",

    @field:PositiveOrZero(message = "Advertiser cost must be zero or a positive value")
    val advertiserCost: Double = 0.0,

    @field:NotBlank(message = "App ID cannot be blank")
    val appId: String = "",

    @field:NotBlank(message = "App title cannot be blank")
    val appTitle: String = "",

    @field:ValidTimestamp
    val impressionTime: Long = 0L
) : AdEventDto
