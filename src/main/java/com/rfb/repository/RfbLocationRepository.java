package com.rfb.repository;

import com.rfb.domain.RfbLocation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RfbLocation entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RfbLocationRepository extends R2dbcRepository<RfbLocation, Long>, RfbLocationRepositoryInternal {
    Flux<RfbLocation> findAllBy(Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<RfbLocation> findAll();

    @Override
    Mono<RfbLocation> findById(Long id);

    @Override
    <S extends RfbLocation> Mono<S> save(S entity);
}

interface RfbLocationRepositoryInternal {
    <S extends RfbLocation> Mono<S> insert(S entity);
    <S extends RfbLocation> Mono<S> save(S entity);
    Mono<Integer> update(RfbLocation entity);

    Flux<RfbLocation> findAll();
    Mono<RfbLocation> findById(Long id);
    Flux<RfbLocation> findAllBy(Pageable pageable);
    Flux<RfbLocation> findAllBy(Pageable pageable, Criteria criteria);
}
