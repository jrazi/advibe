package com.tapsell.platform.ad.adtrack.mapper

import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import jakarta.validation.Valid
import org.springframework.stereotype.Component

@Component
class AdEventMapperImpl : AdEventMapper {
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

    override fun updateFromClick(@Valid clickEvent: ClickEventDto, @Valid adEvent: AdEvent): AdEvent {
        require(adEvent.requestId == clickEvent.requestId) {
            "RequestId must match for both AdEvent and ClickEventDto"
        }
        return adEvent.copy(clickTime = clickEvent.clickTime)
    }

    override fun updateFromImpression(@Valid impressionEvent: ImpressionEventDto, @Valid adEvent: AdEvent): AdEvent {
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
