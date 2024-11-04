package com.tapsell.platform.ad.adstream.job

import com.tapsell.platform.ad.adstream.model.AdInteractionStory
import com.tapsell.platform.ad.contract.dto.AdEventDto
import com.tapsell.platform.ad.eventbus.KafkaEventPublisher
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AdStoryEventScheduler(
    private val taskScheduler: TaskScheduler,
    private val eventPublisher: KafkaEventPublisher
) {
    fun scheduleEventsForStory(story: AdInteractionStory) {
        story.run {
            scheduleIfNotDead(impression, impressionPublishTime, isImpressionDeadLetter)
            scheduleIfNotDead(click, clickPublishTime, isClickDeadLetter)
        }
    }

    private fun scheduleIfNotDead(event: AdEventDto, publishTime: Instant, isDeadLetter: Boolean) {
        if (!isDeadLetter) {
            taskScheduler.schedule({ eventPublisher.publish(event) }, publishTime)
        }
    }
}
