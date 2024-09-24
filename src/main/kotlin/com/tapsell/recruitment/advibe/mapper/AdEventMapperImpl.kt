package com.tapsell.recruitment.advibe.mapper

import com.tapsell.recruitment.advibe.infra.ClickEventDto
import com.tapsell.recruitment.advibe.infra.ImpressionEventDto
import com.tapsell.recruitment.advibe.model.AdEvent
import org.springframework.stereotype.Component

@Component
class AdEventMapperImpl : AdEventMapper {

    override fun toDomain(impressionEvent: ImpressionEventDto): AdEvent {
        return AdEvent(
            requestId = impressionEvent.requestId,
            adId = impressionEvent.adId,
            adTitle = impressionEvent.adTitle,
            advertiserCost = impressionEvent.advertiserCost,
            appId = impressionEvent.appId,
            appTitle = impressionEvent.appTitle,
            impressionTime = impressionEvent.impressionTime,
            clickTime = null
        )
    }

    override fun updateFromClick(clickEvent: ClickEventDto, adEvent: AdEvent): AdEvent {
        // Only updating clickTime as per the requirements
        return adEvent.copy(clickTime = clickEvent.clickTime)
    }

    override fun updateFromImpression(impressionEvent: ImpressionEventDto, adEvent: AdEvent): AdEvent {
        // Updating AdEvent with new values from ImpressionEventDto except clickTime
        return adEvent.copy(
            adId = impressionEvent.adId,
            adTitle = impressionEvent.adTitle,
            advertiserCost = impressionEvent.advertiserCost,
            appId = impressionEvent.appId,
            appTitle = impressionEvent.appTitle,
            impressionTime = impressionEvent.impressionTime
        )
    }
}
