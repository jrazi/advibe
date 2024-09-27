package com.tapsell.platform.ad.eventbus
import com.tapsell.platform.ad.adtrack.consumer.AdEventConsumer
import com.tapsell.platform.ad.contract.dto.AdEventDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, AdEventDto>,
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AdEventConsumer::class.java)
    }

    fun publish(event: AdEventDto) {
        log.debug("Published Event {}", event.requestId)
        kafkaTemplate.send("ad-impression-events", event.requestId, event)
    }
}