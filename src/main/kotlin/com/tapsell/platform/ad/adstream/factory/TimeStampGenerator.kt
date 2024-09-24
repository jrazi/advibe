package com.tapsell.platform.ad.adstream.factory

class TimeStampGenerator(final val timeSampler : NumericalValueSampler<Long>,
                         final val minTime: Long,
                         final val maxTime: Long) : AdEventFieldGenerator<Long> {
    override fun generateValue(): Long {
        return timeSampler.sample(minTime, maxTime)
    }
}