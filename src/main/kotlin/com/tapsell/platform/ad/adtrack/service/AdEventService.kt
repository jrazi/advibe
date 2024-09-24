package com.tapsell.platform.ad.adtrack.service

import com.tapsell.platform.ad.adtrack.mapper.AdEventMapper
import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.adtrack.repository.AdEventRepository
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AdEventService @Autowired constructor(
    private val mapper: AdEventMapper,
    private val eventRepository: AdEventRepository
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun insertImpression(impressionEventDto: ImpressionEventDto): Mono<AdEvent> {
        return Mono.just(impressionEventDto)
            .map(mapper::toDomain)
            .flatMap(eventRepository::save)
            .doOnSuccess { adEvent ->
                logger.info("Impression inserted successfully with id: {}", adEvent.requestId)
            }
            .doOnError { error ->
                logger.error("Error inserting impression with id: {}", impressionEventDto.requestId, error)
            }
    }

    fun updateImpressionWithClickEvent(clickEventDto: ClickEventDto): Mono<AdEvent> {
        return eventRepository.findById(clickEventDto.requestId)
            .switchIfEmpty(
                Mono.error(NoSuchElementException("AdEvent not found with id: ${clickEventDto.requestId}"))
            )
            .flatMap { adEvent ->
                val updatedAdEvent = mapper.updateFromClick(clickEventDto, adEvent)
                eventRepository.save(updatedAdEvent)
            }
            .doOnSuccess { updatedAdEvent ->
                logger.info("Ad event updated with click for id: {}", updatedAdEvent.requestId)
            }
            .doOnError { error ->
                logger.error("Error updating click event for id: {}", clickEventDto.requestId, error)
            }
    }

}
