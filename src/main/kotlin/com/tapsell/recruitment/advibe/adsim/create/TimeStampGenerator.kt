package com.tapsell.recruitment.advibe.adsim.create

class TimeStampGenerator(final val timeSampler : NumericalValueSampler<Long>,
                         final val minTime: Long,
                         final val maxTime: Long) : AdEventFieldGenerator<Long> {
    override fun generateValue(): Long {
        return timeSampler.sample(minTime, maxTime)
    }
}