package com.tapsell.recruitment.advibe.mapper

import com.tapsell.recruitment.advibe.infra.ClickEventDto
import com.tapsell.recruitment.advibe.infra.ImpressionEventDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

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
    fun givenInvalidImpressionEventDto_whenMappingToAdEvent_thenFieldValidationFails() {
        val impressionDto = ImpressionEventDto(
            requestId = "", // Invalid requestId
            adId = "", // Invalid adId
            adTitle = "", // Invalid adTitle
            advertiserCost = -100.0, // Invalid advertiser cost
            appId = "", // Invalid appId
            appTitle = "", // Invalid appTitle
            impressionTime = -1620000000L // Invalid timestamp
        )

        assertThrows(IllegalArgumentException::class.java) {
            mapper.toDomain(impressionDto)
        }
    }

    @Test
    fun givenEmptyImpressionEventDto_whenMapping_thenFieldDefaultsAndValidationApplied() {
        val impressionDto = ImpressionEventDto()

        assertThrows(IllegalArgumentException::class.java) {
            mapper.toDomain(impressionDto)
        }
    }

    @Test
    fun givenExtremeAdvertiserCostInImpressionDto_whenMapping_thenFieldMappedCorrectly() {
        val impressionDto = createValidImpressionDto().copy(advertiserCost = Double.MAX_VALUE/10)

        val adEvent = mapper.toDomain(impressionDto)

        assertEquals(Double.MAX_VALUE, adEvent.advertiserCost)
    }

}
