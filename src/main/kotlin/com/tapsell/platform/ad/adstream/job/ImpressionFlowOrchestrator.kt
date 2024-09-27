package com.tapsell.platform.ad.adstream.job

import com.tapsell.platform.ad.adstream.factory.ClickEventFactory
import com.tapsell.platform.ad.adstream.factory.ImpressionEventFactory
import com.tapsell.platform.ad.eventbus.KafkaEventPublisher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import kotlin.random.Random


/**
 * Currently, runs a job every second, to create and publish K events.
 */
@Component
class ImpressionFlowOrchestrator {

    val EVENT_COUNT_PER_DISPATCH = 2

    @Autowired
    private lateinit var publisher : KafkaEventPublisher

    @Autowired
    private lateinit var impressionEventFactory : ImpressionEventFactory

    @Autowired
    private lateinit var clickEventFactory : ClickEventFactory

    @Scheduled(fixedRate = 5000)
    fun dispatchEvents() {
        for (k in 0 until EVENT_COUNT_PER_DISPATCH) {
            val event = impressionEventFactory.createEvent()
            publisher.publish(event)

            val rand = Random.nextInt()
            if (rand % 10 == 0) {
                val clickEvent = clickEventFactory.createEvent().copy(requestId = event.requestId)
            }
        }
    }

}