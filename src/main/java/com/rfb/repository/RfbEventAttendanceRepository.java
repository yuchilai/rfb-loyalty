package com.rfb.repository;

import com.rfb.domain.RfbEventAttendance;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the RfbEventAttendance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RfbEventAttendanceRepository extends R2dbcRepository<RfbEventAttendance, Long>, RfbEventAttendanceRepositoryInternal {
    Flux<RfbEventAttendance> findAllBy(Pageable pageable);

    @Query("SELECT * FROM rfb_event_attendance entity WHERE entity.rfb_event_id = :id")
    Flux<RfbEventAttendance> findByRfbEvent(Long id);

    @Query("SELECT * FROM rfb_event_attendance entity WHERE entity.rfb_event_id IS NULL")
    Flux<RfbEventAttendance> findAllWhereRfbEventIsNull();

    @Query("SELECT * FROM rfb_event_attendance entity WHERE entity.rfb_user_id = :id")
    Flux<RfbEventAttendance> findByRfbUser(Long id);

    @Query("SELECT * FROM rfb_event_attendance entity WHERE entity.rfb_user_id IS NULL")
    Flux<RfbEventAttendance> findAllWhereRfbUserIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<RfbEventAttendance> findAll();

    @Override
    Mono<RfbEventAttendance> findById(Long id);

    @Override
    <S extends RfbEventAttendance> Mono<S> save(S entity);
}

interface RfbEventAttendanceRepositoryInternal {
    <S extends RfbEventAttendance> Mono<S> insert(S entity);
    <S extends RfbEventAttendance> Mono<S> save(S entity);
    Mono<Integer> update(RfbEventAttendance entity);

    Flux<RfbEventAttendance> findAll();
    Mono<RfbEventAttendance> findById(Long id);
    Flux<RfbEventAttendance> findAllBy(Pageable pageable);
    Flux<RfbEventAttendance> findAllBy(Pageable pageable, Criteria criteria);
}
