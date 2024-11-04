package com.tapsell.platform.ad.adtrack.service

import com.tapsell.platform.ad.adtrack.mapper.ClickEventMapper
import com.tapsell.platform.ad.adtrack.mapper.ImpressionEventMapper
import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.adtrack.repository.AdEventRepository
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Instant

class AdEventServiceTest {

    private lateinit var adEventService: AdEventService
    private lateinit var adEventRepository: AdEventRepository
    private lateinit var impressionMapper: ImpressionEventMapper
    private lateinit var clickMapper: ClickEventMapper

    @BeforeEach
    fun setUp() {
        adEventRepository = mock(AdEventRepository::class.java)
        impressionMapper = mock(ImpressionEventMapper::class.java)
        clickMapper = mock(ClickEventMapper::class.java)
        adEventService = AdEventService(impressionMapper, clickMapper, adEventRepository)
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
    fun `should upsert impression event as new AdEvent`() {
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

        `when`(adEventRepository.findById(impressionDto.requestId)).thenReturn(Mono.empty())
        `when`(impressionMapper.toDomain(impressionDto)).thenReturn(adEvent)
        `when`(adEventRepository.save(adEvent)).thenReturn(Mono.just(adEvent))

        val result = adEventService.upsertImpression(impressionDto)

        StepVerifier.create(result)
            .expectNext(adEvent)
            .verifyComplete()

        verify(impressionMapper).toDomain(impressionDto)
        verify(adEventRepository).save(adEvent)
    }

    @Test
    fun `should update existing AdEvent with new impression data`() {
        val impressionDto = createImpressionEventDto()
        val existingAdEvent = AdEvent(
            requestId = impressionDto.requestId,
            adId = "existingAdId",
            adTitle = "existingAdTitle",
            advertiserCost = 50.0,
            appId = "existingAppId",
            appTitle = "existingAppTitle",
            impressionTime = Instant.now().minusSeconds(60).toEpochMilli(),
            clickTime = null
        )
        val updatedAdEvent = existingAdEvent.copy(
            adId = impressionDto.adId,
            adTitle = impressionDto.adTitle,
            advertiserCost = impressionDto.advertiserCost,
            appId = impressionDto.appId,
            appTitle = impressionDto.appTitle,
            impressionTime = impressionDto.impressionTime
        )

        `when`(adEventRepository.findById(impressionDto.requestId)).thenReturn(Mono.just(existingAdEvent))
        `when`(impressionMapper.updateFromDto(impressionDto, existingAdEvent)).thenReturn(updatedAdEvent)
        `when`(adEventRepository.save(updatedAdEvent)).thenReturn(Mono.just(updatedAdEvent))

        val result = adEventService.upsertImpression(impressionDto)

        StepVerifier.create(result)
            .expectNext(updatedAdEvent)
            .verifyComplete()

        verify(impressionMapper).updateFromDto(impressionDto, existingAdEvent)
        verify(adEventRepository).save(updatedAdEvent)
    }

    @Test
    fun `should upsert click event and update clickTime of existing AdEvent`() {
        val clickDto = createClickEventDto()
        val existingAdEvent = AdEvent(
            requestId = clickDto.requestId,
            adId = "existingAdId",
            adTitle = "existingAdTitle",
            advertiserCost = 100.0,
            appId = "existingAppId",
            appTitle = "existingAppTitle",
            impressionTime = Instant.now().minusSeconds(60).toEpochMilli(),
            clickTime = null
        )
        val updatedAdEvent = existingAdEvent.copy(clickTime = clickDto.clickTime)

        `when`(adEventRepository.findById(clickDto.requestId)).thenReturn(Mono.just(existingAdEvent))
        `when`(clickMapper.updateFromDto(clickDto, existingAdEvent)).thenReturn(updatedAdEvent)
        `when`(adEventRepository.save(updatedAdEvent)).thenReturn(Mono.just(updatedAdEvent))

        val result = adEventService.upsertClickEvent(clickDto)

        StepVerifier.create(result)
            .expectNext(updatedAdEvent)
            .verifyComplete()

        verify(clickMapper).updateFromDto(clickDto, existingAdEvent)
        verify(adEventRepository).save(updatedAdEvent)
    }

}
