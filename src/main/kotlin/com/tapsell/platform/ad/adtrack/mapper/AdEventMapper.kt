package com.tapsell.platform.ad.adtrack.mapper

import com.tapsell.platform.ad.adtrack.model.AdEvent
import jakarta.validation.Valid

interface AdEventMapper<T> {
    fun toDomain(@Valid eventDto: T): AdEvent
    fun updateFromDto(@Valid eventDto: T, @Valid adEvent: AdEvent): AdEvent
}
