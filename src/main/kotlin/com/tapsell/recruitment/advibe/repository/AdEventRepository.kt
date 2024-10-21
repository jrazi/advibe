package com.tapsell.recruitment.advibe.repository

import com.tapsell.recruitment.advibe.model.AdEvent
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AdEventRepository : ReactiveCassandraRepository<AdEvent, String> {

    override fun findById(requestId: String): Mono<AdEvent>
}
