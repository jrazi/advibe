package com.tapsell.recruitment.advibe.infra
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.io.Serializable

@Service
class KafkaEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    fun publish(event: ImpressionEvent) {
        kafkaTemplate.send("ad-impression-events", event.requestId, event.toString())
    }
}