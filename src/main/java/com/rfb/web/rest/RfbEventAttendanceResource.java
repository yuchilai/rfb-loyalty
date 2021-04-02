package com.rfb.web.rest;

import com.rfb.repository.RfbEventAttendanceRepository;
import com.rfb.service.RfbEventAttendanceService;
import com.rfb.service.dto.RfbEventAttendanceDTO;
import com.rfb.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.rfb.domain.RfbEventAttendance}.
 */
@RestController
@RequestMapping("/api")
public class RfbEventAttendanceResource {

    private final Logger log = LoggerFactory.getLogger(RfbEventAttendanceResource.class);

    private static final String ENTITY_NAME = "rfbEventAttendance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RfbEventAttendanceService rfbEventAttendanceService;

    private final RfbEventAttendanceRepository rfbEventAttendanceRepository;

    public RfbEventAttendanceResource(
        RfbEventAttendanceService rfbEventAttendanceService,
        RfbEventAttendanceRepository rfbEventAttendanceRepository
    ) {
        this.rfbEventAttendanceService = rfbEventAttendanceService;
        this.rfbEventAttendanceRepository = rfbEventAttendanceRepository;
    }

    /**
     * {@code POST  /rfb-event-attendances} : Create a new rfbEventAttendance.
     *
     * @param rfbEventAttendanceDTO the rfbEventAttendanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rfbEventAttendanceDTO, or with status {@code 400 (Bad Request)} if the rfbEventAttendance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rfb-event-attendances")
    public Mono<ResponseEntity<RfbEventAttendanceDTO>> createRfbEventAttendance(@RequestBody RfbEventAttendanceDTO rfbEventAttendanceDTO)
        throws URISyntaxException {
        log.debug("REST request to save RfbEventAttendance : {}", rfbEventAttendanceDTO);
        if (rfbEventAttendanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new rfbEventAttendance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rfbEventAttendanceService
            .save(rfbEventAttendanceDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rfb-event-attendances/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rfb-event-attendances/:id} : Updates an existing rfbEventAttendance.
     *
     * @param id the id of the rfbEventAttendanceDTO to save.
     * @param rfbEventAttendanceDTO the rfbEventAttendanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbEventAttendanceDTO,
     * or with status {@code 400 (Bad Request)} if the rfbEventAttendanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rfbEventAttendanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rfb-event-attendances/{id}")
    public Mono<ResponseEntity<RfbEventAttendanceDTO>> updateRfbEventAttendance(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbEventAttendanceDTO rfbEventAttendanceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RfbEventAttendance : {}, {}", id, rfbEventAttendanceDTO);
        if (rfbEventAttendanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbEventAttendanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rfbEventAttendanceRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rfbEventAttendanceService
                        .save(rfbEventAttendanceDTO)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /rfb-event-attendances/:id} : Partial updates given fields of an existing rfbEventAttendance, field will ignore if it is null
     *
     * @param id the id of the rfbEventAttendanceDTO to save.
     * @param rfbEventAttendanceDTO the rfbEventAttendanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbEventAttendanceDTO,
     * or with status {@code 400 (Bad Request)} if the rfbEventAttendanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rfbEventAttendanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rfbEventAttendanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rfb-event-attendances/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RfbEventAttendanceDTO>> partialUpdateRfbEventAttendance(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbEventAttendanceDTO rfbEventAttendanceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RfbEventAttendance partially : {}, {}", id, rfbEventAttendanceDTO);
        if (rfbEventAttendanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbEventAttendanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rfbEventAttendanceRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RfbEventAttendanceDTO> result = rfbEventAttendanceService.partialUpdate(rfbEventAttendanceDTO);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString())
                                    )
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /rfb-event-attendances} : get all the rfbEventAttendances.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rfbEventAttendances in body.
     */
    @GetMapping("/rfb-event-attendances")
    public Mono<ResponseEntity<List<RfbEventAttendanceDTO>>> getAllRfbEventAttendances(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of RfbEventAttendances");
        return rfbEventAttendanceService
            .countAll()
            .zipWith(rfbEventAttendanceService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /rfb-event-attendances/:id} : get the "id" rfbEventAttendance.
     *
     * @param id the id of the rfbEventAttendanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rfbEventAttendanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rfb-event-attendances/{id}")
    public Mono<ResponseEntity<RfbEventAttendanceDTO>> getRfbEventAttendance(@PathVariable Long id) {
        log.debug("REST request to get RfbEventAttendance : {}", id);
        Mono<RfbEventAttendanceDTO> rfbEventAttendanceDTO = rfbEventAttendanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rfbEventAttendanceDTO);
    }

    /**
     * {@code DELETE  /rfb-event-attendances/:id} : delete the "id" rfbEventAttendance.
     *
     * @param id the id of the rfbEventAttendanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rfb-event-attendances/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRfbEventAttendance(@PathVariable Long id) {
        log.debug("REST request to delete RfbEventAttendance : {}", id);
        return rfbEventAttendanceService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
