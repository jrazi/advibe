package com.tapsell.recruitment.advibe.service

import com.tapsell.recruitment.advibe.infra.ClickEventDto
import com.tapsell.recruitment.advibe.infra.ImpressionEventDto
import com.tapsell.recruitment.advibe.model.AdEvent
import com.tapsell.recruitment.advibe.repository.AdEventRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SpringBootTest
class AdEventServiceIntegrationTest {

    @Autowired
    private lateinit var adEventService: AdEventService

    @Autowired
    private lateinit var adEventRepository: AdEventRepository

    @BeforeEach
    fun setUp() {
        adEventRepository.deleteAll().block()
    }

    private fun createImpressionEventDto(
        requestId: String = "testRequestId",
        adId: String = "testAdId",
        adTitle: String = "testAdTitle",
        advertiserCost: Double = 100.0,
        appId: String = "testAppId",
        appTitle: String = "testAppTitle",
        impressionTime: Long = 1620000000L
    ) = ImpressionEventDto(requestId, adId, adTitle, advertiserCost, appId, appTitle, impressionTime)

    private fun createClickEventDto(
        requestId: String = "testRequestId",
        clickTime: Long = 1620000500L
    ) = ClickEventDto(requestId, clickTime)

    @Test
    fun givenValidImpressionEvent_whenInserting_thenAdEventIsSaved() {
        val impressionDto = createImpressionEventDto()

        val result: Mono<AdEvent> = adEventService.insertImpression(impressionDto)

        StepVerifier.create(result)
            .assertNext { adEvent ->
                assert(adEvent.requestId == impressionDto.requestId)
                assert(adEvent.adId == impressionDto.adId)
                assert(adEvent.adTitle == impressionDto.adTitle)
                assert(adEvent.advertiserCost == impressionDto.advertiserCost)
                assert(adEvent.appId == impressionDto.appId)
                assert(adEvent.appTitle == impressionDto.appTitle)
                assert(adEvent.impressionTime == impressionDto.impressionTime)
                assert(adEvent.clickTime == null) // No click time
            }
            .verifyComplete()
    }

    @Test
    fun givenValidClickEvent_whenUpdating_thenClickTimeIsUpdated() {
        val impressionDto = createImpressionEventDto()
        val clickDto = createClickEventDto()

        // First, insert the impression event
        val savedAdEvent = adEventService.insertImpression(impressionDto).block()

        // Now, update with the click event
        val result = adEventService.updateImpressionWithClickEvent(clickDto)

        StepVerifier.create(result)
            .assertNext { adEvent ->
                assert(adEvent.clickTime == clickDto.clickTime)
                assert(adEvent.impressionTime == savedAdEvent?.impressionTime) // Impression fields remain intact
            }
            .verifyComplete()
    }

    @Test
    fun givenClickEvent_whenNoImpressionExists_thenReturnError() {
        val clickDto = createClickEventDto()

        val result = adEventService.updateImpressionWithClickEvent(clickDto)

        StepVerifier.create(result)
            .expectError(IllegalArgumentException::class.java) // No impression exists for the requestId
            .verify()
    }

    @Test
    fun givenImpressionEventArrivesBeforeClick_thenClickUpdatesExistingImpression() {
        val impressionDto = createImpressionEventDto(requestId = "testRequestId1")
        val clickDto = createClickEventDto(requestId = "testRequestId1")

        adEventService.insertImpression(impressionDto).block()

        val result = adEventService.updateImpressionWithClickEvent(clickDto)

        StepVerifier.create(result)
            .assertNext { adEvent ->
                assert(adEvent.requestId == impressionDto.requestId)
                assert(adEvent.clickTime == clickDto.clickTime)
                assert(adEvent.impressionTime == impressionDto.impressionTime)
            }
            .verifyComplete()
    }

    @Test
    fun givenConcurrentClickAndImpressionEvents_thenBothAreHandledCorrectly() {
        val latch = CountDownLatch(2)

        val impressionDto = createImpressionEventDto(requestId = "testConcurrent")
        val clickDto = createClickEventDto(requestId = "testConcurrent")

        val executor = Executors.newFixedThreadPool(2)

        // Concurrently insert impression and update click
        executor.submit {
            adEventService.insertImpression(impressionDto).block()
            latch.countDown()
        }

        executor.submit {
            adEventService.updateImpressionWithClickEvent(clickDto).block()
            latch.countDown()
        }

        latch.await(10, TimeUnit.SECONDS)

        // Verify the final state in the repository
        val result = adEventRepository.findById("testConcurrent")

        StepVerifier.create(result)
            .assertNext { adEvent ->
                assert(adEvent.impressionTime == impressionDto.impressionTime)
                assert(adEvent.clickTime == clickDto.clickTime)
            }
            .verifyComplete()
    }

    @Test
    fun givenMultipleRowsInDatabase_whenQuerying_thenEachRowIsHandledSeparately() {
        val impressionDto1 = createImpressionEventDto(requestId = "testRequestId1")
        val impressionDto2 = createImpressionEventDto(requestId = "testRequestId2")
        val clickDto1 = createClickEventDto(requestId = "testRequestId1")
        val clickDto2 = createClickEventDto(requestId = "testRequestId2")

        // Insert multiple impressions
        adEventService.insertImpression(impressionDto1).block()
        adEventService.insertImpression(impressionDto2).block()

        // Update both with click events
        adEventService.updateImpressionWithClickEvent(clickDto1).block()
        adEventService.updateImpressionWithClickEvent(clickDto2).block()

        // Verify that both rows exist in the database with correct data
        val result1 = adEventRepository.findById("testRequestId1")
        val result2 = adEventRepository.findById("testRequestId2")

        StepVerifier.create(result1)
            .assertNext { adEvent ->
                assert(adEvent.impressionTime == impressionDto1.impressionTime)
                assert(adEvent.clickTime == clickDto1.clickTime)
            }
            .verifyComplete()

        StepVerifier.create(result2)
            .assertNext { adEvent ->
                assert(adEvent.impressionTime == impressionDto2.impressionTime)
                assert(adEvent.clickTime == clickDto2.clickTime)
            }
            .verifyComplete()
    }
}
