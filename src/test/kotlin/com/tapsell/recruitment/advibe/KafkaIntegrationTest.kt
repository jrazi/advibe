package com.tapsell.recruitment.advibe

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = ["test-topic"],
    bootstrapServersProperty = "spring.kafka.bootstrap-servers",
    brokerProperties = ["auto.create.topics.enable=false", "listeners=PLAINTEXT://localhost:9092", "port=9092"]
)
@DirtiesContext
class KafkaIntegrationTest {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @Autowired
    private lateinit var embeddedKafkaBroker: EmbeddedKafkaBroker

    private val KAFKA_TOPIC = "test-topic"

    private lateinit var container: ConcurrentMessageListenerContainer<String, String>

    @BeforeEach
    fun setUp() {
        val consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker)
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        val consumerFactory: ConsumerFactory<String, String> = DefaultKafkaConsumerFactory(consumerProps)
        val containerProps = ContainerProperties(KAFKA_TOPIC)
        containerProps.messageListener = MessageListener<String, String> { record ->
            assert(record.value() == "test-value")
        }
        container = ConcurrentMessageListenerContainer(consumerFactory, containerProps)
        container.start()
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.partitionsPerTopic)
    }

    @AfterEach
    fun tearDown() {
        container.stop()
    }

    @Test
    fun givenEmbeddedKafkaBroker_whenSendMessage_thenMessageIsConsumed() {
        kafkaTemplate.flush()
        val producerRecord = ProducerRecord(KAFKA_TOPIC, "test-key", "test-value")
        val latch = CountDownLatch(1)
        kafkaTemplate.send(producerRecord).thenRunAsync({ latch.countDown() }, { latch.countDown() })
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw AssertionError("Message was not consumed within the timeout")
        }
    }

    @Test
    fun givenMessage_whenSent_thenVerifyProducerAcknowledgment() {
        val producerRecord = ProducerRecord(KAFKA_TOPIC, "ack-key", "ack-value")
        val sendResultMono = Mono.fromFuture(kafkaTemplate.send(producerRecord).toCompletableFuture())
        StepVerifier.create(sendResultMono)
            .expectNextMatches { result ->
                result.producerRecord.value() == "ack-value" &&
                        result.recordMetadata.topic() == KAFKA_TOPIC
            }
            .verifyComplete()
    }

    @Test
    fun givenValidMessage_whenConsuming_thenConsumerReceivesMessage() {
        val message = "hello kafka"
        kafkaTemplate.send(KAFKA_TOPIC, "key", message).get()
        val consumerProps = KafkaTestUtils.consumerProps("test-consumer-group", "true", embeddedKafkaBroker)
        consumerProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        consumerProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(consumerProps)
        val consumer = consumerFactory.createConsumer()
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, KAFKA_TOPIC)
        val records = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10))
        val recordIterator = records.iterator()
        if (recordIterator.hasNext()) {
            val record = recordIterator.next()
            assert(record.value() == message)
        } else {
            throw AssertionError("No message received")
        }
        consumer.close()
    }
}