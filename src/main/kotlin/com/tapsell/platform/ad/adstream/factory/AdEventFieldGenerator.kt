package com.tapsell.platform.ad.adstream.factory

interface AdEventFieldGenerator<T> {
    fun generateValue(): T
}