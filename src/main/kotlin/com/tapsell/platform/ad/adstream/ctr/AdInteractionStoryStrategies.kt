package com.tapsell.platform.ad.adstream.ctr

import java.util.Random
import kotlin.math.pow


interface LetterFateStrategy {
    fun willBeArrived(): Boolean
}

class StaticRatioLetterFateStrategy(private val ratio: Double) : LetterFateStrategy {
    init {
        require(ratio in 0.0..1.0) { "Ratio must be between 0.0 and 1.0" }
    }

    override fun willBeArrived(): Boolean = Random().nextDouble() < ratio
}

interface NetworkLatencyStrategy {
    fun createLatency(): Int
}

class GaussianRandomNetworkLatencyStrategy(
    private val mean: Int,
    private val std: Double
) : NetworkLatencyStrategy {

    init {
        require(mean >= 0) { "Mean latency must be non-negative" }
        require(std >= 0) { "Standard deviation must be non-negative" }
    }

    override fun createLatency(): Int {
        val latency = Random().nextGaussian(mean.toDouble(), std)
        return latency.toInt().coerceAtLeast(0)
    }
}

interface UserClickTimeStrategy {
    fun howMillisLaterWillUserClick(): Int
}

/**
 * Weibull distribution models CTR (Click Through Ratio) with time-dependent click probability.
 * Shape parameter > 1 models increasing click probability over time (e.g., engaging content),
 * Shape = 1 reduces to exponential (constant probability),
 * Shape < 1 models decreasing probability (e.g., banner blindness).
 * Scale parameter determines the spread of the distribution.
 */
class WeibullClickBehaviorStrategy(
    private val mean: Double,
    private val shape: Double,  // k: shape parameter
    private val scale: Double   // λ: scale parameter
) : UserClickTimeStrategy {

    init {
        require(shape > 0.0) { "Shape parameter must be positive" }
        require(scale > 0.0) { "Scale parameter must be positive" }
        require(mean > 0.0) { "Mean must be positive" }
    }

    override fun howMillisLaterWillUserClick(): Int {
        val u = kotlin.random.Random.nextDouble()
        // Weibull inverse CDF formula: F^(-1)(u) = λ * (-ln(1-u))^(1/k)
        val timeMillis = (scale * (-kotlin.math.ln(1 - u)).pow(1.0 / shape)).toLong()
        return timeMillis.toInt()
    }
}

interface UserClickDecisionStrategy {
    fun willTheyClick(): Boolean
}

class StaticCTRBehaviorStrategy(private val ratioPercentage: Int) : UserClickDecisionStrategy {

    init {
        require(ratioPercentage in 0..100) { "Ratio percentage must be between 0 and 100" }
    }

    override fun willTheyClick(): Boolean {
        return (0..100).random() < ratioPercentage
    }
}
