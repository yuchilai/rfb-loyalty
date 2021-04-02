package com.rfb.web.rest;

import com.rfb.repository.RfbEventRepository;
import com.rfb.service.RfbEventService;
import com.rfb.service.dto.RfbEventDTO;
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
 * REST controller for managing {@link com.rfb.domain.RfbEvent}.
 */
@RestController
@RequestMapping("/api")
public class RfbEventResource {

    private final Logger log = LoggerFactory.getLogger(RfbEventResource.class);

    private static final String ENTITY_NAME = "rfbEvent";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RfbEventService rfbEventService;

    private final RfbEventRepository rfbEventRepository;

    public RfbEventResource(RfbEventService rfbEventService, RfbEventRepository rfbEventRepository) {
        this.rfbEventService = rfbEventService;
        this.rfbEventRepository = rfbEventRepository;
    }

    /**
     * {@code POST  /rfb-events} : Create a new rfbEvent.
     *
     * @param rfbEventDTO the rfbEventDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rfbEventDTO, or with status {@code 400 (Bad Request)} if the rfbEvent has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rfb-events")
    public Mono<ResponseEntity<RfbEventDTO>> createRfbEvent(@RequestBody RfbEventDTO rfbEventDTO) throws URISyntaxException {
        log.debug("REST request to save RfbEvent : {}", rfbEventDTO);
        if (rfbEventDTO.getId() != null) {
            throw new BadRequestAlertException("A new rfbEvent cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rfbEventService
            .save(rfbEventDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rfb-events/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rfb-events/:id} : Updates an existing rfbEvent.
     *
     * @param id the id of the rfbEventDTO to save.
     * @param rfbEventDTO the rfbEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbEventDTO,
     * or with status {@code 400 (Bad Request)} if the rfbEventDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rfbEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rfb-events/{id}")
    public Mono<ResponseEntity<RfbEventDTO>> updateRfbEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbEventDTO rfbEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RfbEvent : {}, {}", id, rfbEventDTO);
        if (rfbEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rfbEventRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rfbEventService
                        .save(rfbEventDTO)
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
     * {@code PATCH  /rfb-events/:id} : Partial updates given fields of an existing rfbEvent, field will ignore if it is null
     *
     * @param id the id of the rfbEventDTO to save.
     * @param rfbEventDTO the rfbEventDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbEventDTO,
     * or with status {@code 400 (Bad Request)} if the rfbEventDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rfbEventDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rfbEventDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rfb-events/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RfbEventDTO>> partialUpdateRfbEvent(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbEventDTO rfbEventDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RfbEvent partially : {}, {}", id, rfbEventDTO);
        if (rfbEventDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbEventDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rfbEventRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RfbEventDTO> result = rfbEventService.partialUpdate(rfbEventDTO);

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
     * {@code GET  /rfb-events} : get all the rfbEvents.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rfbEvents in body.
     */
    @GetMapping("/rfb-events")
    public Mono<ResponseEntity<List<RfbEventDTO>>> getAllRfbEvents(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of RfbEvents");
        return rfbEventService
            .countAll()
            .zipWith(rfbEventService.findAll(pageable).collectList())
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
     * {@code GET  /rfb-events/:id} : get the "id" rfbEvent.
     *
     * @param id the id of the rfbEventDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rfbEventDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rfb-events/{id}")
    public Mono<ResponseEntity<RfbEventDTO>> getRfbEvent(@PathVariable Long id) {
        log.debug("REST request to get RfbEvent : {}", id);
        Mono<RfbEventDTO> rfbEventDTO = rfbEventService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rfbEventDTO);
    }

    /**
     * {@code DELETE  /rfb-events/:id} : delete the "id" rfbEvent.
     *
     * @param id the id of the rfbEventDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rfb-events/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRfbEvent(@PathVariable Long id) {
        log.debug("REST request to delete RfbEvent : {}", id);
        return rfbEventService
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
