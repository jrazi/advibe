package com.tapsell.platform.ad.adstream.job

import com.tapsell.platform.ad.adstream.ctr.AdInteractionModelingProperties
import com.tapsell.platform.ad.adstream.ctr.AdStoryMaker
import com.tapsell.platform.ad.eventbus.KafkaEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Runs a job every 5 seconds to create and publish configured events.
 */
@Component
class ImpressionFlowOrchestrator(
    private val publisher: KafkaEventPublisher,
    private val adStoryMaker: AdStoryMaker
) {

    companion object {
        private const val EVENT_COUNT_PER_DISPATCH = 2
    }

    @Scheduled(fixedRate = 5000)
    fun dispatchEvents() {
        repeat(EVENT_COUNT_PER_DISPATCH) {
            val interactionStory = adStoryMaker.createInteractionStory()

            publisher.publish(interactionStory.impression)

            interactionStory.click.let { clickEvent ->
                if (interactionStory.isClickDeadLetter.not()) {
                    publisher.publish(clickEvent)
                }
            }
        }
    }
}
