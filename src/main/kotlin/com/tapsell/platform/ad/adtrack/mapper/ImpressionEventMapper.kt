package com.tapsell.platform.ad.adtrack.mapper

import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import jakarta.validation.Valid
import org.springframework.stereotype.Component

@Component
class ImpressionEventMapper : AdEventMapper<ImpressionEventDto> {
    override fun toDomain(@Valid impressionEvent: ImpressionEventDto): AdEvent {
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

    override fun updateFromDto(@Valid impressionEvent: ImpressionEventDto, @Valid adEvent: AdEvent): AdEvent {
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
