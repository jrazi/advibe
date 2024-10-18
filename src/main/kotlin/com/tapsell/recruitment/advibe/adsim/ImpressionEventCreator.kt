package com.tapsell.recruitment.advibe.adsim

import com.kenai.jffi.Platform
import com.tapsell.recruitment.advibe.infra.ImpressionEventDto
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

@Component
class ImpressionEventCreator {
    fun createValidImpressionEvent() : ImpressionEventDto {
        val impression = ImpressionEventDto(
            requestId = UUID.randomUUID().toString(),
            adId = UUID.randomUUID().toString().substring(8),
            adTitle = Locale.IsoCountryCode.values().random().toString() + ' ' + Random.nextLong().toString(8),
            advertiserCost = (Random.nextFloat() * 1_000_000_000L).toDouble(),
            appId = "TS" + Random.nextBytes(8).asSequence(),
            appTitle = Platform.CPU.values().random().name,
            impressionTime = Instant.now().toEpochMilli()
        )
        return impression
    }
}