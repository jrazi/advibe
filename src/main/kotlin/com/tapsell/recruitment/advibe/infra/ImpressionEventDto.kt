package com.tapsell.recruitment.advibe.infra

data class ImpressionEventDto(
    override val requestId: String = "",
    val adId: String = "",
    val adTitle: String = "",
    val advertiserCost: Double = 0.0,
    val appId: String = "",
    val appTitle: String = "",
    val impressionTime: Long = 0L
) : AdEventDto
