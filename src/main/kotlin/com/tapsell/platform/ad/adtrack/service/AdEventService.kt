package com.tapsell.platform.ad.adtrack.service

import com.tapsell.platform.ad.adtrack.mapper.ClickEventMapper
import com.tapsell.platform.ad.adtrack.mapper.ImpressionEventMapper
import com.tapsell.platform.ad.adtrack.model.AdEvent
import com.tapsell.platform.ad.adtrack.repository.AdEventRepository
import com.tapsell.platform.ad.contract.dto.ClickEventDto
import com.tapsell.platform.ad.contract.dto.ImpressionEventDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AdEventService(
    private val impressionMapper: ImpressionEventMapper,
    private val clickMapper: ClickEventMapper,
    private val eventRepository: AdEventRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun upsertImpression(impressionEventDto: ImpressionEventDto): Mono<AdEvent> =
        eventRepository.findById(impressionEventDto.requestId)
            .flatMap { existingEvent ->
                eventRepository.save(impressionMapper.updateFromDto(impressionEventDto, existingEvent))
            }
            .switchIfEmpty(
                Mono.just(impressionEventDto)
                    .map(impressionMapper::toDomain)
                    .flatMap(eventRepository::save)
            )
            .doOnSuccess { logger.info("Upsert impression successful for id: ${it.requestId}") }
            .doOnError { logger.error("Error upserting impression for id: ${impressionEventDto.requestId}", it) }

    fun upsertClickEvent(clickEventDto: ClickEventDto): Mono<AdEvent> =
        eventRepository.findById(clickEventDto.requestId)
            .flatMap { existingEvent ->
                eventRepository.save(clickMapper.updateFromDto(clickEventDto, existingEvent))
            }
            .switchIfEmpty(
                Mono.just(clickEventDto)
                    .map(clickMapper::toDomain)
                    .flatMap(eventRepository::save)
            )
            .doOnSuccess { logger.info("Upsert click successful for id: ${it.requestId}") }
            .doOnError { logger.error("Error upserting click for id: ${clickEventDto.requestId}", it) }
}
