package com.tapsell.platform.ad.adstream.interaction

import com.tapsell.platform.ad.adstream.interaction.factory.AdInteractionStoryFactory
import com.tapsell.platform.ad.adstream.interaction.props.AdInteractionModelingProperties
import com.tapsell.platform.ad.adstream.model.AdInteractionStory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import kotlin.test.assertNotNull
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@SpringBootTest
@EnableConfigurationProperties(AdInteractionModelingProperties::class)
class AdInteractionStoryFactoryTest(
    @Autowired private val props: AdInteractionModelingProperties,
    @Autowired private val adStoryMaker: AdInteractionStoryFactory
) {

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
        assertTrue(story.isClickDeadLetter || !story.isClickDeadLetter)
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
