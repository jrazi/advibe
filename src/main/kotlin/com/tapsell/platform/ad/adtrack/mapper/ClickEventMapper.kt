package com.tapsell.platform.ad.adtrack.mapper

import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import jakarta.validation.Valid
import org.springframework.stereotype.Component

@Component
class ClickEventMapper : AdEventMapper<ClickEventDto> {
    override fun toDomain(@Valid clickEvent: ClickEventDto): AdEvent {
        return AdEvent(
            requestId = clickEvent.requestId,
            adId = "",
            adTitle = "",
            advertiserCost = 0.0,
            appId = "",
            appTitle = "",
            impressionTime = null,
            clickTime = clickEvent.clickTime
        )
    }

    override fun updateFromDto(@Valid clickEvent: ClickEventDto, @Valid adEvent: AdEvent): AdEvent {
        require(adEvent.requestId == clickEvent.requestId) {
            "RequestId must match for both AdEvent and ClickEventDto"
        }
        return adEvent.copy(clickTime = clickEvent.clickTime)
    }
}
