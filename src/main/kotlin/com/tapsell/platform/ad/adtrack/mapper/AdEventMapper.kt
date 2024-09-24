package com.tapsell.platform.ad.adtrack.mapper

import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import jakarta.validation.Valid

interface AdEventMapper {

    fun toDomain(@Valid impressionEvent: ImpressionEventDto): AdEvent

    fun updateFromClick(@Valid clickEvent: ClickEventDto, @Valid adEvent: AdEvent): AdEvent

    fun updateFromImpression(@Valid impressionEvent: ImpressionEventDto, @Valid adEvent: AdEvent): AdEvent
}
