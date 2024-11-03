package com.tapsell.platform.ad.adstream.job

import com.tapsell.platform.ad.adstream.model.AdInteractionStory
import com.tapsell.platform.ad.eventbus.KafkaEventPublisher
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component


@Component
class AdStoryEventScheduler(
    private val taskScheduler: TaskScheduler,
    private val eventPublisher: KafkaEventPublisher
) {
    fun scheduleEventsForStory(story: AdInteractionStory) {
        // Schedule impression if not dead letter
        if (!story.isImpressionDeadLetter) {
            taskScheduler.schedule(
                { eventPublisher.publish(story.impression) },
                story.impressionPublishTime
            )
        }

        // Schedule click if not dead letter
        if (!story.isClickDeadLetter) {
            taskScheduler.schedule(
                { eventPublisher.publish(story.click) },
                story.clickPublishTime
            )
        }
    }
}
