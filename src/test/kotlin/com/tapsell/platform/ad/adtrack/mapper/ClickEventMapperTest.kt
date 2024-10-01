package com.tapsell.platform.ad.adtrack.mapper

import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClickEventMapperTest {

    private val mapper = ClickEventMapper()

    private fun createValidClickDto() = ClickEventDto(
        requestId = "validRequestId",
        clickTime = 1620000500L
    )

    @Test
    fun `should map ClickEventDto to AdEvent with only clickTime set`() {
        val clickDto = createValidClickDto()
        val adEvent = mapper.toDomain(clickDto)

        assertNotNull(adEvent)
        assertEquals(clickDto.requestId, adEvent.requestId)
        assertEquals(clickDto.clickTime, adEvent.clickTime)
        assertEquals("", adEvent.adId)
        assertEquals("", adEvent.adTitle)
        assertEquals(0.0, adEvent.advertiserCost)
        assertEquals("", adEvent.appId)
        assertEquals("", adEvent.appTitle)
        assertNull(adEvent.impressionTime, "Expected impressionTime to be null")
    }

    @Test
    fun `should update existing AdEvent with ClickEventDto clickTime only`() {
        val clickDto = createValidClickDto()
        var adEvent = AdEvent(
            requestId = "validRequestId",
            adId = "existingAdId",
            adTitle = "existingAdTitle",
            advertiserCost = 50.0,
            appId = "existingAppId",
            appTitle = "existingAppTitle",
            impressionTime = 1620000000L,
            clickTime = null
        )

        adEvent = mapper.updateFromDto(clickDto, adEvent)

        assertNotNull(adEvent)
        assertEquals(clickDto.requestId, adEvent.requestId)
        assertEquals("existingAdId", adEvent.adId)
        assertEquals("existingAdTitle", adEvent.adTitle)
        assertEquals(50.0, adEvent.advertiserCost)
        assertEquals("existingAppId", adEvent.appId)
        assertEquals("existingAppTitle", adEvent.appTitle)
        assertEquals(1620000000L, adEvent.impressionTime)
        assertEquals(clickDto.clickTime, adEvent.clickTime)
    }

    @Test
    fun `should throw exception if ClickEventDto requestId does not match AdEvent requestId`() {
        val clickDto = createValidClickDto().copy(requestId = "differentRequestId")
        val adEvent = AdEvent(
            requestId = "validRequestId",
            adId = "existingAdId",
            adTitle = "existingAdTitle",
            advertiserCost = 50.0,
            appId = "existingAppId",
            appTitle = "existingAppTitle",
            impressionTime = 1620000000L,
            clickTime = null
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            mapper.updateFromDto(clickDto, adEvent)
        }

        assertEquals("RequestId must match for both AdEvent and ClickEventDto", exception.message)
    }
}
