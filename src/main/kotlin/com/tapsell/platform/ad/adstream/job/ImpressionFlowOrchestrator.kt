package com.tapsell.platform.ad.adstream.job

import com.tapsell.platform.ad.adstream.ctr.AdInteractionModelingProperties
import com.tapsell.platform.ad.adstream.ctr.AdInteractionPublishingProperties
import com.tapsell.platform.ad.adstream.ctr.AdStoryMaker
import com.tapsell.platform.ad.eventbus.KafkaEventPublisher
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Runs a job every 5 seconds to create and publish configured events.
 */
@Component
@PropertySource("classpath:application.yaml")
class ImpressionFlowOrchestrator(
    private val storyMaker: AdStoryMaker,
    private val eventScheduler: AdStoryEventScheduler,
    private val properties: AdInteractionPublishingProperties
) {

    @Scheduled(
        fixedRateString = "\${ad-interaction.publishing.eventDispatchScheduleRunningIntervalMillis}"
    )
    fun scheduleNextBatchOfInteractions() {
        repeat(properties.eventScheduledForDispatchCount) {
            val story = storyMaker.createInteractionStory()
            eventScheduler.scheduleEventsForStory(story)
        }
    }
}
