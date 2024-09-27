package com.tapsell.platform.ad.adstream.factory

import com.tapsell.platform.ad.contract.dto.AdEventDto

interface EventFactory<T : AdEventDto> {
    fun createEvent() : T
}