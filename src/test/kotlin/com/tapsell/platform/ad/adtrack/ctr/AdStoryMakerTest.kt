package com.tapsell.platform.ad.adstream.ctr

import com.tapsell.platform.ad.adstream.model.AdInteractionStory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.test.assertNotNull
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@SpringBootTest
@EnableConfigurationProperties(AdInteractionModelingProperties::class)
class AdStoryMakerTest(
    @Autowired private val props: AdInteractionModelingProperties
) {

    private val adStoryMaker: AdStoryMaker by lazy { AdStoryMaker(props) }

    @Test
    fun `given AdStoryMaker when creating interaction story then story has valid properties`() {
        // when
        val story: AdInteractionStory = adStoryMaker.createInteractionStory()

        // then
        assertNotNull(story.impression)
        assertNotNull(story.click)

        assertTrue(story.impressionPublishTime.isAfter(Instant.parse("2000-01-01T00:00:00Z")) &&
                story.impressionPublishTime.isBefore(Instant.parse("2100-01-01T00:00:00Z")),
            "Impression publish time should be in a realistic range")

        assertTrue(story.clickPublishTime.isAfter(story.impressionPublishTime) ||
                story.clickPublishTime == story.impressionPublishTime,
            "Click publish time should follow impression publish time")

        assertTrue(story.isImpressionDeadLetter || !story.isImpressionDeadLetter)
        assertTrue(story.clickDeadLetter || !story.clickDeadLetter)
    }

    @Test
    fun `given AdStoryMaker when creating multiple stories then click times should vary`() {
        // when
        val story1 = adStoryMaker.createInteractionStory()
        val story2 = adStoryMaker.createInteractionStory()

        // then
        assertNotEquals(story1.clickPublishTime, story2.clickPublishTime, "Click publish times should vary")
    }
}
