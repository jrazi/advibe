package com.tapsell.platform.ad.adtrack.mapper

import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ImpressionEventMapperTest {

    private val mapper = ImpressionEventMapper()

    private fun createValidImpressionDto() = ImpressionEventDto(
        requestId = "validRequestId",
        adId = "validAdId",
        adTitle = "validAdTitle",
        advertiserCost = 100.0,
        appId = "validAppId",
        appTitle = "validAppTitle",
        impressionTime = 1620000000L
    )

    @Test
    fun `should map ImpressionEventDto to AdEvent correctly`() {
        val impressionDto = createValidImpressionDto()
        val adEvent = mapper.toDomain(impressionDto)

        assertNotNull(adEvent)
        assertEquals(impressionDto.requestId, adEvent.requestId)
        assertEquals(impressionDto.adId, adEvent.adId)
        assertEquals(impressionDto.adTitle, adEvent.adTitle)
        assertEquals(impressionDto.advertiserCost, adEvent.advertiserCost)
        assertEquals(impressionDto.appId, adEvent.appId)
        assertEquals(impressionDto.appTitle, adEvent.appTitle)
        assertEquals(impressionDto.impressionTime, adEvent.impressionTime)
        assertNull(adEvent.clickTime, "Expected clickTime to be null")
    }

    @Test
    fun `should update existing AdEvent with ImpressionEventDto fields correctly`() {
        val impressionDto = createValidImpressionDto()
        var adEvent = mapper.toDomain(impressionDto).copy(clickTime = 1620000500L)

        adEvent = mapper.updateFromDto(impressionDto, adEvent)

        assertNotNull(adEvent)
        assertEquals(impressionDto.requestId, adEvent.requestId)
        assertEquals(impressionDto.adId, adEvent.adId)
        assertEquals(impressionDto.adTitle, adEvent.adTitle)
        assertEquals(impressionDto.advertiserCost, adEvent.advertiserCost)
        assertEquals(impressionDto.appId, adEvent.appId)
        assertEquals(impressionDto.appTitle, adEvent.appTitle)
        assertEquals(impressionDto.impressionTime, adEvent.impressionTime)
        assertEquals(1620000500L, adEvent.clickTime, "Expected clickTime to remain unchanged")
    }

    @Test
    fun `should handle large advertiserCost in ImpressionEventDto`() {
        val impressionDto = createValidImpressionDto().copy(advertiserCost = Double.MAX_VALUE / 100)
        val adEvent = mapper.toDomain(impressionDto)

        assertEquals(Double.MAX_VALUE / 100, adEvent.advertiserCost)
    }
}
