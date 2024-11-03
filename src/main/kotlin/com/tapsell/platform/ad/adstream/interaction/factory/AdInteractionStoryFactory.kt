package com.tapsell.platform.ad.adstream.interaction.factory

import com.tapsell.platform.ad.adstream.interaction.strategy.LetterFateStrategy
import com.tapsell.platform.ad.adstream.interaction.strategy.StaticCTRBehaviorStrategy
import com.tapsell.platform.ad.adstream.interaction.strategy.StaticRatioLetterFateStrategy
import com.tapsell.platform.ad.adstream.interaction.strategy.UserClickDecisionStrategy
import com.tapsell.platform.ad.adstream.interaction.strategy.UserClickTimeStrategy
import com.tapsell.platform.ad.adstream.interaction.strategy.WeibullClickBehaviorStrategy
import com.tapsell.platform.ad.adstream.model.AdInteractionStory
import com.tapsell.platform.ad.adstream.interaction.props.AdInteractionModelingProperties
import org.springframework.stereotype.Component
import java.time.Instant

interface AdInteractionStoryFactory {
    fun createInteractionStory(): AdInteractionStory
}

class StrategyBasedAdInteractionStoryFactory(
    private val clickFactory: ClickEventFactory,
    private val impressionFactory: ImpressionEventFactory,
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
            impression = impressionFactory.createEvent(),  // Replace with real impression instance
            click = clickFactory.createEvent(),        // Replace with real click factory instance
            impressionPublishTime = baseTimeForLetterPublish,
            clickPublishTime = clickTime ?: baseTimeForLetterPublish,
            isImpressionDeadLetter = isImpressionDeadLetter,
            isClickDeadLetter = isClickDeadLetter
        )
    }
}

@Component
class PropsBasedAdInteractionStoryFactory(
    private val props: AdInteractionModelingProperties,
    private val clickFactory: ClickEventFactory,
    private val impressionFactory: ImpressionEventFactory
) : AdInteractionStoryFactory {

    override fun createInteractionStory(): AdInteractionStory {
        val clickStrategy = StaticCTRBehaviorStrategy(props.clickThroughRate)
        val clickTimeStrategy =
            WeibullClickBehaviorStrategy(props.meanClickTime.toDouble(), props.clickTimeShape, props.clickTimeScale)
        val impressionFateStrategy = StaticRatioLetterFateStrategy(props.impressionArrivalRatio)
        val clickFateStrategy = StaticRatioLetterFateStrategy(props.clickArrivalRatio)
        val baseFactory = StrategyBasedAdInteractionStoryFactory(
            clickStrategy = clickStrategy,
            clickTimeStrategy = clickTimeStrategy,
            impressionFateStrategy = impressionFateStrategy,
            clickFateStrategy = clickFateStrategy,
            baseTimeForLetterPublish = Instant.now(),
            clickFactory = clickFactory,
            impressionFactory = impressionFactory
        )

        return baseFactory.createInteractionStory()
    }
}
