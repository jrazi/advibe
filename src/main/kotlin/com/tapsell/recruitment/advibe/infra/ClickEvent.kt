package com.tapsell.recruitment.advibe.infra

import java.io.Serializable

data class ClickEvent(
    val requestId: String,
    val clickTime: Long
) : Serializable
