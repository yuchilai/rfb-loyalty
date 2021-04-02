package com.rfb.service;

import com.rfb.service.dto.RfbEventAttendanceDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.rfb.domain.RfbEventAttendance}.
 */
public interface RfbEventAttendanceService {
    /**
     * Save a rfbEventAttendance.
     *
     * @param rfbEventAttendanceDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<RfbEventAttendanceDTO> save(RfbEventAttendanceDTO rfbEventAttendanceDTO);

    /**
     * Partially updates a rfbEventAttendance.
     *
     * @param rfbEventAttendanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<RfbEventAttendanceDTO> partialUpdate(RfbEventAttendanceDTO rfbEventAttendanceDTO);

    /**
     * Get all the rfbEventAttendances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<RfbEventAttendanceDTO> findAll(Pageable pageable);

    /**
     * Returns the number of rfbEventAttendances available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" rfbEventAttendance.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<RfbEventAttendanceDTO> findOne(Long id);

    /**
     * Delete the "id" rfbEventAttendance.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
