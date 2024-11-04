package com.tapsell.platform.ad.adstream.ctr

import com.tapsell.platform.ad.adstream.props.CTRProperties
import kotlin.random.Random

interface CTRStrategy {
    fun shouldClick(): Boolean
}

class StaticCTRStrategy(private val mean: Double) : CTRStrategy {
    override fun shouldClick(): Boolean {
        return Random.nextDouble() < mean
    }
}

object CTRStrategyFactory {
    fun create(ctrProperties: CTRProperties): CTRStrategy {
        return when (ctrProperties.mode) {
            "static" -> StaticCTRStrategy(ctrProperties.mean)
            else -> throw IllegalArgumentException("Unsupported CTR mode: ${ctrProperties.mode}")
        }
    }
}
