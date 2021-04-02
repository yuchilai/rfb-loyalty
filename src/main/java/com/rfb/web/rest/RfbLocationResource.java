package com.rfb.web.rest;

import com.rfb.repository.RfbLocationRepository;
import com.rfb.service.RfbLocationService;
import com.rfb.service.dto.RfbLocationDTO;
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
 * REST controller for managing {@link com.rfb.domain.RfbLocation}.
 */
@RestController
@RequestMapping("/api")
public class RfbLocationResource {

    private final Logger log = LoggerFactory.getLogger(RfbLocationResource.class);

    private static final String ENTITY_NAME = "rfbLocation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RfbLocationService rfbLocationService;

    private final RfbLocationRepository rfbLocationRepository;

    public RfbLocationResource(RfbLocationService rfbLocationService, RfbLocationRepository rfbLocationRepository) {
        this.rfbLocationService = rfbLocationService;
        this.rfbLocationRepository = rfbLocationRepository;
    }

    /**
     * {@code POST  /rfb-locations} : Create a new rfbLocation.
     *
     * @param rfbLocationDTO the rfbLocationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new rfbLocationDTO, or with status {@code 400 (Bad Request)} if the rfbLocation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/rfb-locations")
    public Mono<ResponseEntity<RfbLocationDTO>> createRfbLocation(@RequestBody RfbLocationDTO rfbLocationDTO) throws URISyntaxException {
        log.debug("REST request to save RfbLocation : {}", rfbLocationDTO);
        if (rfbLocationDTO.getId() != null) {
            throw new BadRequestAlertException("A new rfbLocation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return rfbLocationService
            .save(rfbLocationDTO)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/rfb-locations/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /rfb-locations/:id} : Updates an existing rfbLocation.
     *
     * @param id the id of the rfbLocationDTO to save.
     * @param rfbLocationDTO the rfbLocationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbLocationDTO,
     * or with status {@code 400 (Bad Request)} if the rfbLocationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the rfbLocationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/rfb-locations/{id}")
    public Mono<ResponseEntity<RfbLocationDTO>> updateRfbLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbLocationDTO rfbLocationDTO
    ) throws URISyntaxException {
        log.debug("REST request to update RfbLocation : {}, {}", id, rfbLocationDTO);
        if (rfbLocationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbLocationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rfbLocationRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return rfbLocationService
                        .save(rfbLocationDTO)
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
     * {@code PATCH  /rfb-locations/:id} : Partial updates given fields of an existing rfbLocation, field will ignore if it is null
     *
     * @param id the id of the rfbLocationDTO to save.
     * @param rfbLocationDTO the rfbLocationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated rfbLocationDTO,
     * or with status {@code 400 (Bad Request)} if the rfbLocationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the rfbLocationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the rfbLocationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/rfb-locations/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<RfbLocationDTO>> partialUpdateRfbLocation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RfbLocationDTO rfbLocationDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update RfbLocation partially : {}, {}", id, rfbLocationDTO);
        if (rfbLocationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, rfbLocationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return rfbLocationRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<RfbLocationDTO> result = rfbLocationService.partialUpdate(rfbLocationDTO);

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
     * {@code GET  /rfb-locations} : get all the rfbLocations.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of rfbLocations in body.
     */
    @GetMapping("/rfb-locations")
    public Mono<ResponseEntity<List<RfbLocationDTO>>> getAllRfbLocations(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of RfbLocations");
        return rfbLocationService
            .countAll()
            .zipWith(rfbLocationService.findAll(pageable).collectList())
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
     * {@code GET  /rfb-locations/:id} : get the "id" rfbLocation.
     *
     * @param id the id of the rfbLocationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the rfbLocationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/rfb-locations/{id}")
    public Mono<ResponseEntity<RfbLocationDTO>> getRfbLocation(@PathVariable Long id) {
        log.debug("REST request to get RfbLocation : {}", id);
        Mono<RfbLocationDTO> rfbLocationDTO = rfbLocationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(rfbLocationDTO);
    }

    /**
     * {@code DELETE  /rfb-locations/:id} : delete the "id" rfbLocation.
     *
     * @param id the id of the rfbLocationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/rfb-locations/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteRfbLocation(@PathVariable Long id) {
        log.debug("REST request to delete RfbLocation : {}", id);
        return rfbLocationService
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
