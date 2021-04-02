package com.rfb.service;

import com.rfb.service.dto.RfbEventDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.rfb.domain.RfbEvent}.
 */
public interface RfbEventService {
    /**
     * Save a rfbEvent.
     *
     * @param rfbEventDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RfbEventDTO> save(RfbEventDTO rfbEventDTO);

    /**
     * Partially updates a rfbEvent.
     *
     * @param rfbEventDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RfbEventDTO> partialUpdate(RfbEventDTO rfbEventDTO);

    /**
     * Get all the rfbEvents.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RfbEventDTO> findAll(Pageable pageable);

    /**
     * Returns the number of rfbEvents available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rfbEvent.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RfbEventDTO> findOne(Long id);

    /**
     * Delete the "id" rfbEvent.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
