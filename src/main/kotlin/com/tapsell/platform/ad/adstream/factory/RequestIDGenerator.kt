package com.tapsell.platform.ad.adstream.factory

import java.util.UUID

class RequestIDGenerator : AdEventFieldGenerator<String> {
    override fun generateValue(): String {
        return UUID.randomUUID().toString()
    }
}