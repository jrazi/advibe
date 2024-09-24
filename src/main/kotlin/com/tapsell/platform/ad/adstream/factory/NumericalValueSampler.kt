package com.tapsell.platform.ad.adstream.factory

interface NumericalValueSampler<T : Number> {
    fun sample(min: T, max: T): T
}