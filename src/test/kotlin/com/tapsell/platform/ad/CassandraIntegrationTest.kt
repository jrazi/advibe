package com.tapsell.platform.ad

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate
import org.springframework.data.cassandra.core.mapping.PrimaryKey
import org.springframework.data.cassandra.core.mapping.Table
import org.springframework.data.cassandra.core.query.Criteria
import org.springframework.data.cassandra.core.query.Query
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@DataCassandraTest
@DirtiesContext(classMode = BEFORE_CLASS)
class CassandraIntegrationTest {

    @Autowired
    private lateinit var reactiveCassandraTemplate: ReactiveCassandraTemplate

    @Table("sample_entity")
    data class SampleEntity(
        @PrimaryKey val id: String,
        val data: String
    )

    @Test
    fun givenValidEntity_whenSave_thenEntityIsSaved() {
        val sampleEntity = SampleEntity("1", "Sample Data")
        val saveMono: Mono<SampleEntity> = reactiveCassandraTemplate.insert(sampleEntity)

        StepVerifier.create(saveMono)
            .expectNextMatches { it.id == "1" && it.data == "Sample Data" }
            .verifyComplete()

        // Verify that the entity can be retrieved after saving
        val findMono: Mono<SampleEntity> = reactiveCassandraTemplate.selectOne(
            Query.query(Criteria.where("id").`is`("1")), SampleEntity::class.java
        )

        StepVerifier.create(findMono)
            .expectNextMatches { it.id == "1" && it.data == "Sample Data" }
            .verifyComplete()
    }

    @Test
    fun givenSavedEntity_whenFindById_thenEntityIsFound() {
        val sampleEntity = SampleEntity("2", "Another Sample Data")
        val saveMono: Mono<SampleEntity> = reactiveCassandraTemplate.insert(sampleEntity)

        StepVerifier.create(saveMono)
            .expectNextMatches { it.id == "2" && it.data == "Another Sample Data" }
            .verifyComplete()

        val findMono: Mono<SampleEntity> = reactiveCassandraTemplate.selectOne(
            Query.query(Criteria.where("id").`is`("2")), SampleEntity::class.java
        )

        StepVerifier.create(findMono)
            .expectNextMatches { it.id == "2" && it.data == "Another Sample Data" }
            .verifyComplete()
    }

    @Test
    fun givenNonExistentEntity_whenFindById_thenEntityIsNotFound() {
        val findMono: Mono<SampleEntity> = reactiveCassandraTemplate.selectOne(
            Query.query(Criteria.where("id").`is`("non-existent-id")), SampleEntity::class.java
        )

        StepVerifier.create(findMono)
            .expectNextCount(0) // Expect no results for a non-existent ID
            .verifyComplete()
    }
}