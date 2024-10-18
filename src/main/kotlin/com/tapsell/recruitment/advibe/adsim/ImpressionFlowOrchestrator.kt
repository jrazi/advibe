package com.tapsell.recruitment.advibe.adsim

import com.tapsell.recruitment.advibe.infra.KafkaEventPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


/**
 * Currently, runs a job every second, to create and publish K events.
 */
@Component
class ImpressionFlowOrchestrator {

    val EVENT_COUNT_PER_DISPATCH = 10

    @Autowired
    private lateinit var publisher : KafkaEventPublisher

    @Autowired
    private lateinit var impressionEventCreator : ImpressionEventCreator

    @Scheduled(fixedRate = 1000)
    fun dispatchEvents() {
        for (k in 0 until EVENT_COUNT_PER_DISPATCH) {
            val event = impressionEventCreator.createValidImpressionEvent()
            publisher.publish(event)
        }
    }

}