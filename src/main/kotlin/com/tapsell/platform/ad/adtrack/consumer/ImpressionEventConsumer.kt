package com.tapsell.platform.ad.adtrack.consumer

import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.slf4j.Logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class ImpressionEventConsumer {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ImpressionEventConsumer::class.java)
    }

    @KafkaListener(topics = ["ad-impression-events"], groupId = "advibe-consumer-group")
    fun listenImpressionEvents(event: ImpressionEventDto) {
        log.info("Received Impression Event: $event")
    }
}