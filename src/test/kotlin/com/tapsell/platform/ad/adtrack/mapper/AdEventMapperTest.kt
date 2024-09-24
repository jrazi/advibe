package com.tapsell.platform.ad.adtrack.mapper

import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AdEventMapperTest {

    private val mapper: AdEventMapper = AdEventMapperImpl()

    // Helper to create valid ImpressionEventDto
    private fun createValidImpressionDto() = ImpressionEventDto(
        requestId = "validRequestId",
        adId = "validAdId",
        adTitle = "validAdTitle",
        advertiserCost = 100.0,
        appId = "validAppId",
        appTitle = "validAppTitle",
        impressionTime = 1620000000L
    )

    // Helper to create valid ClickEventDto
    private fun createValidClickDto() = ClickEventDto(
        requestId = "validRequestId",
        clickTime = 1620000500L
    )

    @Test
    fun givenValidImpressionEventDto_whenMappingToAdEvent_thenAllFieldsMappedCorrectly() {
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
        assertNull(adEvent.clickTime) // clickTime should not be set
    }

    @Test
    fun givenValidClickEventDto_whenUpdatingExistingAdEvent_thenOnlyClickTimeIsUpdated() {
        val impressionDto = createValidImpressionDto()
        var adEvent = mapper.toDomain(impressionDto)
        val clickDto = createValidClickDto()

        adEvent = mapper.updateFromClick(clickDto, adEvent)

        assertNotNull(adEvent)
        assertEquals(impressionDto.requestId, adEvent.requestId)
        assertEquals(impressionDto.adId, adEvent.adId) // Ensure impression fields are preserved
        assertEquals(impressionDto.adTitle, adEvent.adTitle)
        assertEquals(impressionDto.advertiserCost, adEvent.advertiserCost)
        assertEquals(impressionDto.appId, adEvent.appId)
        assertEquals(impressionDto.appTitle, adEvent.appTitle)
        assertEquals(impressionDto.impressionTime, adEvent.impressionTime)
        assertEquals(clickDto.clickTime, adEvent.clickTime) // clickTime should be updated
    }

    @Test
    fun givenExtremeAdvertiserCostInImpressionDto_whenMapping_thenFieldMappedCorrectly() {
        val impressionDto = createValidImpressionDto().copy(advertiserCost = Double.MAX_VALUE/100)

        val adEvent = mapper.toDomain(impressionDto)

        assertEquals(Double.MAX_VALUE/100, adEvent.advertiserCost)
    }

}
