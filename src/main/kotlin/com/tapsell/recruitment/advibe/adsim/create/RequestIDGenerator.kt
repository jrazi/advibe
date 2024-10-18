package com.tapsell.recruitment.advibe.adsim.create

import java.util.UUID

class RequestIDGenerator : AdEventFieldGenerator<String> {
    override fun generateValue(): String {
        return UUID.randomUUID().toString()
    }
}