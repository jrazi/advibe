package com.tapsell.recruitment.advibe.infra
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, AdEventDto>,
) {
    fun publish(event: AdEventDto) {
        kafkaTemplate.send("ad-impression-events", event.requestId, event)
    }
}