package com.tapsell.recruitment.advibe.adsim.create

interface AdEventFieldGenerator<T> {
    fun generateValue(): T
}