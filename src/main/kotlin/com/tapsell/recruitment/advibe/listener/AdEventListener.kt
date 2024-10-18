package com.tapsell.recruitment.advibe.listener

import org.slf4j.Logger
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory

@Service
class AdEventListener {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(AdEventListener::class.java)
    }

    @KafkaListener(topics = ["impressions"], groupId = "advibe-consumer-group")
    fun listenImpressionEvents(message: String) {
        log.info("Received Impression Event: $message")
        // TODO For now, we just log the message. Further processing will be implemented later.
    }
}