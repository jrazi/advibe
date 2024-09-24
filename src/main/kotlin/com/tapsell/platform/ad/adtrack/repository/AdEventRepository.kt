package com.tapsell.platform.ad.adtrack.repository

import com.tapsell.platform.ad.adtrack.model.AdEvent
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AdEventRepository : ReactiveCassandraRepository<AdEvent, String> {

    override fun findById(requestId: String): Mono<AdEvent>
}
