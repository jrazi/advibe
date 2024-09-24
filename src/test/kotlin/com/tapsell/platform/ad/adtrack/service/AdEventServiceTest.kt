package com.tapsell.platform.ad.adtrack.service

import com.tapsell.platform.ad.adtrack.mapper.AdEventMapper
import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.adtrack.repository.AdEventRepository
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.junit.jupiter.api.*
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

@SpringBootTest
class AdEventServiceTest {

    private lateinit var adEventService: AdEventService
    private lateinit var adEventRepository: AdEventRepository
    private lateinit var adEventMapper: AdEventMapper

    @BeforeEach
    fun setUp() {
        adEventRepository = mock(AdEventRepository::class.java)
        adEventMapper = mock(AdEventMapper::class.java)
        adEventService = AdEventService(adEventMapper, adEventRepository)
    }

    private fun createImpressionEventDto(
        requestId: String = "testRequestId",
        adId: String = "testAdId",
        adTitle: String = "testAdTitle",
        advertiserCost: Double = 100.0,
        appId: String = "testAppId",
        appTitle: String = "testAppTitle",
        impressionTime: Long = Instant.now().toEpochMilli()
    ) = ImpressionEventDto(requestId, adId, adTitle, advertiserCost, appId, appTitle, impressionTime)

    private fun createClickEventDto(
        requestId: String = "testRequestId",
        clickTime: Long = Instant.now().toEpochMilli()
    ) = ClickEventDto(requestId, clickTime)

    @Test
    fun `given valid impression event when inserting then AdEvent is saved`() {
        val impressionDto = createImpressionEventDto()
        val adEvent = AdEvent(
            requestId = impressionDto.requestId,
            adId = impressionDto.adId,
            adTitle = impressionDto.adTitle,
            advertiserCost = impressionDto.advertiserCost,
            appId = impressionDto.appId,
            appTitle = impressionDto.appTitle,
            impressionTime = impressionDto.impressionTime,
            clickTime = null
        )

        `when`(adEventMapper.toDomain(impressionDto)).thenReturn(adEvent)
        `when`(adEventRepository.save(adEvent)).thenReturn(Mono.just(adEvent))

        val result = adEventService.insertImpression(impressionDto)

        StepVerifier.create(result)
            .assertNext { savedAdEvent ->
                Assertions.assertEquals(adEvent, savedAdEvent)
            }
            .verifyComplete()

        verify(adEventMapper).toDomain(impressionDto)
        verify(adEventRepository).save(adEvent)
    }

    @Test
    fun `given valid click event when updating then clickTime is updated`() {
        val clickDto = createClickEventDto()
        val existingAdEvent = AdEvent(
            requestId = clickDto.requestId,
            adId = "testAdId",
            adTitle = "testAdTitle",
            advertiserCost = 100.0,
            appId = "testAppId",
            appTitle = "testAppTitle",
            impressionTime = Instant.now().minusSeconds(60).toEpochMilli(),
            clickTime = null
        )
        val updatedAdEvent = existingAdEvent.copy(clickTime = clickDto.clickTime)

        `when`(adEventRepository.findById(clickDto.requestId)).thenReturn(Mono.just(existingAdEvent))
        `when`(adEventMapper.updateFromClick(clickDto, existingAdEvent)).thenReturn(updatedAdEvent)
        `when`(adEventRepository.save(updatedAdEvent)).thenReturn(Mono.just(updatedAdEvent))

        val result = adEventService.updateImpressionWithClickEvent(clickDto)

        StepVerifier.create(result)
            .assertNext { adEvent ->
                Assertions.assertEquals(updatedAdEvent, adEvent)
            }
            .verifyComplete()

        verify(adEventRepository).findById(clickDto.requestId)
        verify(adEventMapper).updateFromClick(clickDto, existingAdEvent)
        verify(adEventRepository).save(updatedAdEvent)
    }

    @Test
    fun `given click event when no impression exists then return error`() {
        val clickDto = createClickEventDto()

        `when`(adEventRepository.findById(clickDto.requestId)).thenReturn(Mono.empty())

        val result = adEventService.updateImpressionWithClickEvent(clickDto)

        StepVerifier.create(result)
            .expectErrorMatches { throwable ->
                throwable is NoSuchElementException && throwable.message == "AdEvent not found with id: ${clickDto.requestId}"
            }
            .verify()

        verify(adEventRepository).findById(clickDto.requestId)
        verifyNoMoreInteractions(adEventMapper, adEventRepository)
    }

    @Test
    fun `given multiple events when processing then each is handled correctly`() {
        val impressionDto1 = createImpressionEventDto(requestId = "requestId1")
        val impressionDto2 = createImpressionEventDto(requestId = "requestId2")
        val clickDto1 = createClickEventDto(requestId = "requestId1")
        val clickDto2 = createClickEventDto(requestId = "requestId2")

        val adEvent1 = AdEvent(
            requestId = impressionDto1.requestId,
            adId = impressionDto1.adId,
            adTitle = impressionDto1.adTitle,
            advertiserCost = impressionDto1.advertiserCost,
            appId = impressionDto1.appId,
            appTitle = impressionDto1.appTitle,
            impressionTime = impressionDto1.impressionTime,
            clickTime = null
        )
        val adEvent2 = AdEvent(
            requestId = impressionDto2.requestId,
            adId = impressionDto2.adId,
            adTitle = impressionDto2.adTitle,
            advertiserCost = impressionDto2.advertiserCost,
            appId = impressionDto2.appId,
            appTitle = impressionDto2.appTitle,
            impressionTime = impressionDto2.impressionTime,
            clickTime = null
        )
        val updatedAdEvent1 = adEvent1.copy(clickTime = clickDto1.clickTime)
        val updatedAdEvent2 = adEvent2.copy(clickTime = clickDto2.clickTime)

        // Mock insertions
        `when`(adEventMapper.toDomain(impressionDto1)).thenReturn(adEvent1)
        `when`(adEventMapper.toDomain(impressionDto2)).thenReturn(adEvent2)
        `when`(adEventRepository.save(adEvent1)).thenReturn(Mono.just(adEvent1))
        `when`(adEventRepository.save(adEvent2)).thenReturn(Mono.just(adEvent2))

        // Mock updates
        `when`(adEventRepository.findById(clickDto1.requestId)).thenReturn(Mono.just(adEvent1))
        `when`(adEventRepository.findById(clickDto2.requestId)).thenReturn(Mono.just(adEvent2))
        `when`(adEventMapper.updateFromClick(clickDto1, adEvent1)).thenReturn(updatedAdEvent1)
        `when`(adEventMapper.updateFromClick(clickDto2, adEvent2)).thenReturn(updatedAdEvent2)
        `when`(adEventRepository.save(updatedAdEvent1)).thenReturn(Mono.just(updatedAdEvent1))
        `when`(adEventRepository.save(updatedAdEvent2)).thenReturn(Mono.just(updatedAdEvent2))

        // Insert impressions
        val insert1 = adEventService.insertImpression(impressionDto1)
        val insert2 = adEventService.insertImpression(impressionDto2)

        // Update with clicks
        val update1 = adEventService.updateImpressionWithClickEvent(clickDto1)
        val update2 = adEventService.updateImpressionWithClickEvent(clickDto2)

        StepVerifier.create(insert1)
            .expectNext(adEvent1)
            .verifyComplete()

        StepVerifier.create(insert2)
            .expectNext(adEvent2)
            .verifyComplete()

        StepVerifier.create(update1)
            .expectNext(updatedAdEvent1)
            .verifyComplete()

        StepVerifier.create(update2)
            .expectNext(updatedAdEvent2)
            .verifyComplete()
    }
}
