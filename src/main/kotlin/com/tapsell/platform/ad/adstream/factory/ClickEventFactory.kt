package com.tapsell.platform.ad.adstream.factory

import com.tapsell.platform.ad.contract.dto.ClickEventDto
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class ClickEventFactory : EventFactory<ClickEventDto> {
    override fun createEvent(): ClickEventDto {
        return ClickEventDto(
            requestId = UUID.randomUUID().toString().substring(2),
            clickTime = Instant.now().toEpochMilli()
        )
    }
}