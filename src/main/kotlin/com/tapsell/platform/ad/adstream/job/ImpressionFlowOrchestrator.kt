import com.tapsell.platform.ad.adstream.ctr.CTRStrategy
import com.tapsell.platform.ad.adstream.ctr.CTRStrategyFactory
import com.tapsell.platform.ad.adstream.interaction.factory.ClickEventFactory
import com.tapsell.platform.ad.adstream.interaction.factory.ImpressionEventFactory
import com.tapsell.platform.ad.adstream.props.AdStreamProperties
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import com.tapsell.platform.ad.eventbus.KafkaEventPublisher
import kotlinx.coroutines.*
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.Random

@Component
class ImpressionFlowOrchestrator(
    private val publisher: KafkaEventPublisher,
    private val impressionEventFactory: ImpressionEventFactory,
    private val clickEventFactory: ClickEventFactory,
    private val adStreamProperties: AdStreamProperties
) {

    private val ctrStrategy: CTRStrategy = CTRStrategyFactory.create(adStreamProperties.ctr)
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    @Scheduled(fixedRate = 5000)
    fun dispatchEvents() {
        val eventCount = adStreamProperties.impression.eventCountPerDispatch
        for (k in 0 until eventCount) {
            val impressionEvent = impressionEventFactory.createEvent()
            publisher.publish(impressionEvent)

            if (ctrStrategy.shouldClick()) {
                scheduleClickEvent(impressionEvent)
            }
        }
    }

    private fun scheduleClickEvent(impressionEvent: ImpressionEventDto) {
        val timeToInterest = generateTimeToInterest()
        coroutineScope.launch {
            delay(timeToInterest.toLong())
            val clickEvent = clickEventFactory.createEvent().copy(requestId = impressionEvent.requestId)
            publisher.publish(clickEvent)
        }
    }

    private fun generateTimeToInterest(): Double {
        val mean = adStreamProperties.impression.timeToInterest.mean
        val std = adStreamProperties.impression.timeToInterest.std
        val time = Random().nextGaussian(mean, std)
        return maxOf(time, 100.0) // Ensuring time is at least 100 milliseconds later
    }
}
