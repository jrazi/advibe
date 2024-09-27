package com.tapsell.platform.ad.eventbus
import com.tapsell.platform.ad.adtrack.consumer.AdEventConsumer
import com.tapsell.platform.ad.contract.dto.AdEventDto
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
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
        when(event) {
            is ImpressionEventDto ->
                kafkaTemplate.send("ad-impression-events", event.requestId, event)
            is ClickEventDto ->
                kafkaTemplate.send("ad-click-events", event.requestId, event)
        }
        log.debug("Published Event {}", event.requestId)
    }
}