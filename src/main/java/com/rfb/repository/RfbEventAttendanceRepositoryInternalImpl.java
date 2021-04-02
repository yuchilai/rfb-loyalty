package com.rfb.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.rfb.domain.RfbEventAttendance;
import com.rfb.repository.rowmapper.RfbEventAttendanceRowMapper;
import com.rfb.repository.rowmapper.RfbEventRowMapper;
import com.rfb.repository.rowmapper.RfbUserRowMapper;
import com.rfb.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the RfbEventAttendance entity.
 */
@SuppressWarnings("unused")
class RfbEventAttendanceRepositoryInternalImpl implements RfbEventAttendanceRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RfbEventRowMapper rfbeventMapper;
    private final RfbUserRowMapper rfbuserMapper;
    private final RfbEventAttendanceRowMapper rfbeventattendanceMapper;

    private static final Table entityTable = Table.aliased("rfb_event_attendance", EntityManager.ENTITY_ALIAS);
    private static final Table rfbEventTable = Table.aliased("rfb_event", "rfbEvent");
    private static final Table rfbUserTable = Table.aliased("rfb_user", "rfbUser");

    public RfbEventAttendanceRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RfbEventRowMapper rfbeventMapper,
        RfbUserRowMapper rfbuserMapper,
        RfbEventAttendanceRowMapper rfbeventattendanceMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rfbeventMapper = rfbeventMapper;
        this.rfbuserMapper = rfbuserMapper;
        this.rfbeventattendanceMapper = rfbeventattendanceMapper;
    }

    @Override
    public Flux<RfbEventAttendance> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RfbEventAttendance> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RfbEventAttendance> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RfbEventAttendanceSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RfbEventSqlHelper.getColumns(rfbEventTable, "rfbEvent"));
        columns.addAll(RfbUserSqlHelper.getColumns(rfbUserTable, "rfbUser"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(rfbEventTable)
            .on(Column.create("rfb_event_id", entityTable))
            .equals(Column.create("id", rfbEventTable))
            .leftOuterJoin(rfbUserTable)
            .on(Column.create("rfb_user_id", entityTable))
            .equals(Column.create("id", rfbUserTable));

        String select = entityManager.createSelect(selectFrom, RfbEventAttendance.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<RfbEventAttendance> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RfbEventAttendance> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private RfbEventAttendance process(Row row, RowMetadata metadata) {
        RfbEventAttendance entity = rfbeventattendanceMapper.apply(row, "e");
        entity.setRfbEvent(rfbeventMapper.apply(row, "rfbEvent"));
        entity.setRfbUser(rfbuserMapper.apply(row, "rfbUser"));
        return entity;
    }

    @Override
    public <S extends RfbEventAttendance> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RfbEventAttendance> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RfbEventAttendance with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(RfbEventAttendance entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class RfbEventAttendanceSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("attendance_date", table, columnPrefix + "_attendance_date"));

        columns.add(Column.aliased("rfb_event_id", table, columnPrefix + "_rfb_event_id"));
        columns.add(Column.aliased("rfb_user_id", table, columnPrefix + "_rfb_user_id"));
        return columns;
    }
}
