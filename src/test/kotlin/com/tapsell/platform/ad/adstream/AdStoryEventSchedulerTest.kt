package com.tapsell.platform.ad.adstream

import com.tapsell.platform.ad.adstream.job.AdStoryEventScheduler
import com.tapsell.platform.ad.adstream.model.AdInteractionStory
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import com.tapsell.platform.ad.eventbus.KafkaEventPublisher
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.*
import org.springframework.scheduling.TaskScheduler
import java.time.Instant
import kotlin.test.assertFailsWith

class AdStoryEventSchedulerTest {

    private lateinit var taskScheduler: TaskScheduler
    private lateinit var eventPublisher: KafkaEventPublisher
    private lateinit var adStoryEventScheduler: AdStoryEventScheduler

    @BeforeEach
    fun setup() {
        taskScheduler = mock()
        eventPublisher = mock()
        adStoryEventScheduler = AdStoryEventScheduler(taskScheduler, eventPublisher)
    }

    @Test
    fun `should schedule impression and click events when both are not dead letters`() {
        val story = AdInteractionStory(
            impression = ImpressionEventDto(requestId = "imp123"),
            click = ClickEventDto(requestId = "clk123"),
            impressionPublishTime = Instant.now().plusSeconds(10),
            clickPublishTime = Instant.now().plusSeconds(20),
            isImpressionDeadLetter = false,
            isClickDeadLetter = false
        )

        adStoryEventScheduler.scheduleEventsForStory(story)

        // Capture scheduled tasks
        verify(taskScheduler).schedule(any(), eq(story.impressionPublishTime))
        verify(taskScheduler).schedule(any(), eq(story.clickPublishTime))
        verify(eventPublisher, never()).publish(any())
    }

    @Test
    fun `should only schedule impression when click is dead letter`() {
        val story = AdInteractionStory(
            impression = ImpressionEventDto(requestId = "1235"),
            click = ClickEventDto(requestId = "1235"),
            impressionPublishTime = Instant.now().plusSeconds(10),
            clickPublishTime = Instant.now().plusSeconds(20),
            isImpressionDeadLetter = false,
            isClickDeadLetter = true
        )

        adStoryEventScheduler.scheduleEventsForStory(story)

        verify(taskScheduler).schedule(any(), eq(story.impressionPublishTime))
        verify(taskScheduler, never()).schedule(any(), eq(story.clickPublishTime))
        verify(eventPublisher, never()).publish(any())
    }

    @Test
    fun `should only schedule click when impression is dead letter`() {
        val story = AdInteractionStory(
            impression = ImpressionEventDto(requestId = "imp123"),
            click = ClickEventDto(requestId = "clk123"),
            impressionPublishTime = Instant.now().plusSeconds(10),
            clickPublishTime = Instant.now().plusSeconds(20),
            isImpressionDeadLetter = true,
            isClickDeadLetter = false
        )

        adStoryEventScheduler.scheduleEventsForStory(story)

        verify(taskScheduler, never()).schedule(any(), eq(story.impressionPublishTime))
        verify(taskScheduler).schedule(any(), eq(story.clickPublishTime))
        verify(eventPublisher, never()).publish(any())
    }

    @Test
    fun `should not schedule any events when both are dead letters`() {
        val story = AdInteractionStory(
            impression = ImpressionEventDto(requestId = "imp123"),
            click = ClickEventDto(requestId = "clk123"),
            impressionPublishTime = Instant.now().plusSeconds(10),
            clickPublishTime = Instant.now().plusSeconds(20),
            isImpressionDeadLetter = true,
            isClickDeadLetter = true
        )

        adStoryEventScheduler.scheduleEventsForStory(story)

        verify(taskScheduler, never()).schedule(any<Runnable>(), any<Instant>())
        verify(eventPublisher, never()).publish(any())
    }

    @Test
    fun `should throw exception when scheduling fails`() {
        val story = AdInteractionStory(
            impression = ImpressionEventDto(requestId = "impression"),
            click = ClickEventDto(requestId = "impression"),
            impressionPublishTime = Instant.now().plusSeconds(10),
            clickPublishTime = Instant.now().plusSeconds(20),
            isImpressionDeadLetter = false,
            isClickDeadLetter = false
        )

        whenever(taskScheduler.schedule(any(), eq(story.impressionPublishTime))).thenThrow(RuntimeException("Scheduler failure"))

        assertFailsWith<RuntimeException> {
            adStoryEventScheduler.scheduleEventsForStory(story)
        }

        verify(taskScheduler).schedule(any(), eq(story.impressionPublishTime))
        verify(taskScheduler, never()).schedule(any(), eq(story.clickPublishTime))
    }
}
