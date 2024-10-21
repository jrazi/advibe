package com.tapsell.recruitment.advibe.service

import com.tapsell.recruitment.advibe.infra.ClickEventDto
import com.tapsell.recruitment.advibe.infra.ImpressionEventDto
import com.tapsell.recruitment.advibe.mapper.AdEventMapper
import com.tapsell.recruitment.advibe.model.AdEvent
import com.tapsell.recruitment.advibe.repository.AdEventRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AdEventService {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AdEventService::class.java)
    }

    @Autowired
    lateinit var mapper: AdEventMapper

    @Autowired
    lateinit var eventRepository: AdEventRepository

    fun insertImpression(impressionEventDto: ImpressionEventDto): Mono<AdEvent> {
        val adEvent = mapper.toDomain(impressionEventDto)
        return eventRepository.save(adEvent)
            .doOnSuccess { logger.info("Impression inserted successfully with id: {}", adEvent.requestId) }
            .doOnError { error -> logger.error("Error inserting impression with id: {}. Error: {}", adEvent.requestId, error.message) }
    }

    fun updateImpressionWithClickEvent(clickEventDto: ClickEventDto): Mono<AdEvent> {
        return eventRepository.findById(clickEventDto.requestId)
            .flatMap { adEvent ->
                val updatedAdEvent = mapper.updateFromClick(clickEventDto, adEvent)
                eventRepository.save(updatedAdEvent)
            }
            .doOnSuccess { logger.info("Ad event updated with click for id: {}", clickEventDto.requestId) }
            .doOnError { error -> logger.error("Error updating click event for id: {}. Error: {}", clickEventDto.requestId, error.message) }
    }

}
