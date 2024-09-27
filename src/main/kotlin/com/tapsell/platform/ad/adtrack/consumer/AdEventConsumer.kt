package com.tapsell.platform.ad.adtrack.consumer

import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.slf4j.Logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class AdEventConsumer {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AdEventConsumer::class.java)
    }

    @KafkaListener(topics = ["ad-impression-events"], groupId = "advibe-consumer-group")
    fun listenImpressionEvents(event: ImpressionEventDto) {
        log.debug("Received Impression Event: {}", event)
    }

    @KafkaListener(topics = ["click-events"], groupId = "advibe-consumer-group")
    fun listenClickEvents(event: ClickEventDto) {
        log.debug("Received Click Event {}", event)
    }
}