package com.rfb.service;

import com.rfb.service.dto.RfbUserDTO;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.rfb.domain.RfbUser}.
 */
public interface RfbUserService {
    /**
     * Save a rfbUser.
     *
     * @param rfbUserDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RfbUserDTO> save(RfbUserDTO rfbUserDTO);

    /**
     * Partially updates a rfbUser.
     *
     * @param rfbUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RfbUserDTO> partialUpdate(RfbUserDTO rfbUserDTO);

    /**
     * Get all the rfbUsers.
     *
     * @return the list of entities.
     */
    Flux<RfbUserDTO> findAll();

    /**
     * Returns the number of rfbUsers available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rfbUser.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RfbUserDTO> findOne(Long id);

    /**
     * Delete the "id" rfbUser.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
