package com.tapsell.recruitment.advibe.infra

data class ClickEventDto(
    override val requestId: String,
    val clickTime: Long
) : AdEventDto
