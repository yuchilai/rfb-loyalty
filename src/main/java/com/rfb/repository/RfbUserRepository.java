package com.rfb.repository;

import com.rfb.domain.RfbUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RfbUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RfbUserRepository extends R2dbcRepository<RfbUser, Long>, RfbUserRepositoryInternal {
    @Query("SELECT * FROM rfb_user entity WHERE entity.home_location_id = :id")
    Flux<RfbUser> findByHomeLocation(Long id);

    @Query("SELECT * FROM rfb_user entity WHERE entity.home_location_id IS NULL")
    Flux<RfbUser> findAllWhereHomeLocationIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<RfbUser> findAll();

    @Override
    Mono<RfbUser> findById(Long id);

    @Override
    <S extends RfbUser> Mono<S> save(S entity);
}

interface RfbUserRepositoryInternal {
    <S extends RfbUser> Mono<S> insert(S entity);
    <S extends RfbUser> Mono<S> save(S entity);
    Mono<Integer> update(RfbUser entity);

    Flux<RfbUser> findAll();
    Mono<RfbUser> findById(Long id);
    Flux<RfbUser> findAllBy(Pageable pageable);
    Flux<RfbUser> findAllBy(Pageable pageable, Criteria criteria);
}
