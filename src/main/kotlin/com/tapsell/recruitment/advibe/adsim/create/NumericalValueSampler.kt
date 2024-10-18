package com.tapsell.recruitment.advibe.adsim.create

interface NumericalValueSampler<T : Number> {
    fun sample(min: T, max: T): T
}