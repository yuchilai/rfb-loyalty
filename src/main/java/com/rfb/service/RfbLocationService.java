package com.rfb.service;

import com.rfb.service.dto.RfbLocationDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.rfb.domain.RfbLocation}.
 */
public interface RfbLocationService {
    /**
     * Save a rfbLocation.
     *
     * @param rfbLocationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RfbLocationDTO> save(RfbLocationDTO rfbLocationDTO);

    /**
     * Partially updates a rfbLocation.
     *
     * @param rfbLocationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RfbLocationDTO> partialUpdate(RfbLocationDTO rfbLocationDTO);

    /**
     * Get all the rfbLocations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RfbLocationDTO> findAll(Pageable pageable);

    /**
     * Returns the number of rfbLocations available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rfbLocation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RfbLocationDTO> findOne(Long id);

    /**
     * Delete the "id" rfbLocation.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
