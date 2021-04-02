package com.rfb.repository;

import com.rfb.domain.RfbEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RfbEvent entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RfbEventRepository extends R2dbcRepository<RfbEvent, Long>, RfbEventRepositoryInternal {
    Flux<RfbEvent> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rfb_event entity WHERE entity.rfb_location_id = :id")
    Flux<RfbEvent> findByRfbLocation(Long id);

    @Query("SELECT * FROM rfb_event entity WHERE entity.rfb_location_id IS NULL")
    Flux<RfbEvent> findAllWhereRfbLocationIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<RfbEvent> findAll();

    @Override
    Mono<RfbEvent> findById(Long id);

    @Override
    <S extends RfbEvent> Mono<S> save(S entity);
}

interface RfbEventRepositoryInternal {
    <S extends RfbEvent> Mono<S> insert(S entity);
    <S extends RfbEvent> Mono<S> save(S entity);
    Mono<Integer> update(RfbEvent entity);

    Flux<RfbEvent> findAll();
    Mono<RfbEvent> findById(Long id);
    Flux<RfbEvent> findAllBy(Pageable pageable);
    Flux<RfbEvent> findAllBy(Pageable pageable, Criteria criteria);
}
