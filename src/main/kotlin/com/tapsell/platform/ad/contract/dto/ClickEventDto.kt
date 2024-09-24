package com.tapsell.platform.ad.contract.dto

import com.tapsell.platform.ad.contract.validation.ValidTimestamp
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.NotBlank

data class ClickEventDto(
    @field:NotBlank(message = "Request ID cannot be blank")
    override val requestId: String,

    @field:NotNull(message = "Click time cannot be null")
    @field:ValidTimestamp
    val clickTime: Long
) : AdEventDto
