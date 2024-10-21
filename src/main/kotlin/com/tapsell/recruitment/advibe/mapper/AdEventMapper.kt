package com.tapsell.recruitment.advibe.mapper

import com.tapsell.recruitment.advibe.infra.ClickEventDto
import com.tapsell.recruitment.advibe.infra.ImpressionEventDto
import com.tapsell.recruitment.advibe.model.AdEvent

interface AdEventMapper {

    fun toDomain(impressionEvent: ImpressionEventDto): AdEvent

    fun updateFromClick(clickEvent: ClickEventDto, adEvent: AdEvent): AdEvent

    fun updateFromImpression(impressionEvent: ImpressionEventDto, adEvent: AdEvent): AdEvent
}
