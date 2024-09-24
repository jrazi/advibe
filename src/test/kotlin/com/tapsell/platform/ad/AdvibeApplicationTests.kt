package com.tapsell.platform.ad

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.kafka.test.context.EmbeddedKafka

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9093", "port=9093"])
class AdvibeApplicationTests {

	@Test
	fun contextLoads() {
	}

    companion object {
        @DynamicPropertySource
        @JvmStatic
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { "localhost:9093" }
        }
    }

}
