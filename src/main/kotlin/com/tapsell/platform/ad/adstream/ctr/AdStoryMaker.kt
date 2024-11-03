package com.tapsell.platform.ad.adstream.ctr

import com.tapsell.platform.ad.adstream.model.AdInteractionStory
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import com.tapsell.platform.ad.adstream.factory.ClickEventFactory
import java.time.Instant

interface AdInteractionStoryFactory {
    fun createInteractionStory(): AdInteractionStory
}

class StrategyBasedAdInteractionStoryFactory(
    private val clickStrategy: UserClickDecisionStrategy,
    private val clickTimeStrategy: UserClickTimeStrategy,
    private val impressionFateStrategy: LetterFateStrategy,
    private val clickFateStrategy: LetterFateStrategy,
    private val baseTimeForLetterPublish: Instant
) : AdInteractionStoryFactory {

    override fun createInteractionStory(): AdInteractionStory {
        val willClick = clickStrategy.willTheyClick()
        val clickTime = if (willClick) baseTimeForLetterPublish.plusMillis(clickTimeStrategy.howMillisLaterWillUserClick().toLong()) else null
        val isImpressionDeadLetter = !impressionFateStrategy.willBeArrived()
        val isClickDeadLetter = !clickFateStrategy.willBeArrived()

        return AdInteractionStory(
            impression = ImpressionEventDto(),  // Replace with real impression instance
            click = ClickEventFactory(),        // Replace with real click factory instance
            impressionPublishTime = baseTimeForLetterPublish,
            clickPublishTime = clickTime ?: baseTimeForLetterPublish,
            isImpressionDeadLetter = isImpressionDeadLetter,
            clickDeadLetter = isClickDeadLetter
        )
    }
}

class AdStoryMaker(
    private val props: AdInteractionModelingProperties
) : AdInteractionStoryFactory {

    override fun createInteractionStory(): AdInteractionStory {
        val clickStrategy = StaticCTRBehaviorStrategy(props.clickThroughRate)
        val clickTimeStrategy = WeibullClickBehaviorStrategy(props.meanClickTime.toDouble(), props.clickTimeShape, props.clickTimeScale)
        val impressionFateStrategy = StaticRatioLetterFateStrategy(props.impressionArrivalRatio)
        val clickFateStrategy = StaticRatioLetterFateStrategy(props.clickArrivalRatio)
        val baseFactory = StrategyBasedAdInteractionStoryFactory(
            clickStrategy = clickStrategy,
            clickTimeStrategy = clickTimeStrategy,
            impressionFateStrategy = impressionFateStrategy,
            clickFateStrategy = clickFateStrategy,
            baseTimeForLetterPublish = Instant.now()
        )

        return baseFactory.createInteractionStory()
    }
}
