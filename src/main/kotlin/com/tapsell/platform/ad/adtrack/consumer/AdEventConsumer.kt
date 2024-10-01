package com.tapsell.platform.ad.adtrack.consumer

import com.tapsell.platform.ad.adtrack.service.AdEventService
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.slf4j.Logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AdEventConsumer(private val adEventService: AdEventService) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AdEventConsumer::class.java)
    }

    @KafkaListener(topicPattern = "ad-impression-events.*", groupId = "advibe-consumer-group")
    fun listenImpressionEvents(event: ImpressionEventDto) {
        adEventService.upsertImpression(event)
            .doOnSuccess { log.info("Impression event upserted for id: ${event.requestId}") }
            .doOnError { e -> log.error("Failed to upsert impression event for id: ${event.requestId}", e) }
            .subscribe()
    }

    @KafkaListener(topicPattern = "ad-click-events.*", groupId = "advibe-consumer-group")
    fun listenClickEvents(event: ClickEventDto) {
        adEventService.upsertClickEvent(event)
            .doOnSuccess { log.info("Click event upserted for id: ${event.requestId}") }
            .doOnError { e -> log.error("Failed to upsert click event for id: ${event.requestId}", e) }
            .subscribe()
    }
}
