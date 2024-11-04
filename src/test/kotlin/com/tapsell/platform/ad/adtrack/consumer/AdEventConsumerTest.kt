package com.tapsell.platform.ad.adtrack.consumer

import com.tapsell.platform.ad.adtrack.service.AdEventService
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.timeout
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import reactor.core.publisher.Mono

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = ["ad-impression-events", "ad-click-events"],
    brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "auto.create.topics.enable=false"]
)
@ExtendWith(MockitoExtension::class)
class AdEventConsumerTest {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Mock
    private lateinit var adEventService: AdEventService

    @Captor
    private lateinit var impressionCaptor: ArgumentCaptor<ImpressionEventDto>

    @Captor
    private lateinit var clickCaptor: ArgumentCaptor<ClickEventDto>

    private lateinit var impressionEventDto: ImpressionEventDto
    private lateinit var clickEventDto: ClickEventDto

    @BeforeEach
    fun setup() {
        impressionEventDto = ImpressionEventDto(
            requestId = "12345",
            adId = "ad-001",
            adTitle = "Sample Ad",
            advertiserCost = 0.5,
            appId = "app-001",
            appTitle = "Sample App",
            impressionTime = System.currentTimeMillis()
        )
        clickEventDto = ClickEventDto(
            requestId = "12345",
            clickTime = System.currentTimeMillis()
        )
        val producerConfigs = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        val producerFactory: ProducerFactory<String, Any> = DefaultKafkaProducerFactory(producerConfigs)
        kafkaTemplate = KafkaTemplate(producerFactory)
    }

    @Test
    fun `should invoke impression listener`() {
        `when`(adEventService.upsertImpression(any(ImpressionEventDto::class.java))).thenReturn(Mono.empty())

        kafkaTemplate.send("ad-impression-events", impressionEventDto)

        Thread.sleep(2000)

        verify(adEventService, timeout(5000)).upsertImpression(impressionCaptor.capture())
    }

    @Test
    fun `should invoke click listener`() {
        `when`(adEventService.upsertClickEvent(any(ClickEventDto::class.java))).thenReturn(Mono.empty())

        kafkaTemplate.send("ad-click-events", clickEventDto)

        Thread.sleep(2000)

        verify(adEventService, timeout(5000)).upsertClickEvent(clickCaptor.capture())
    }
}
