package com.tapsell.platform.ad.adstream.model

import com.tapsell.platform.ad.adstream.factory.ClickEventFactory
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import java.time.Instant

data class AdInteractionStory(
    val impression: ImpressionEventDto,
    val click: ClickEventFactory,
    val impressionPublishTime: Instant,
    val clickPublishTime: Instant,
    val isImpressionDeadLetter: Boolean,
    val clickDeadLetter: Boolean
)
