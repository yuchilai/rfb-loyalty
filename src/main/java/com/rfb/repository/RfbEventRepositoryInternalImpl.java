package com.rfb.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.rfb.domain.RfbEvent;
import com.rfb.repository.rowmapper.RfbEventRowMapper;
import com.rfb.repository.rowmapper.RfbLocationRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the RfbEvent entity.
 */
@SuppressWarnings("unused")
class RfbEventRepositoryInternalImpl implements RfbEventRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RfbLocationRowMapper rfblocationMapper;
    private final RfbEventRowMapper rfbeventMapper;

    private static final Table entityTable = Table.aliased("rfb_event", EntityManager.ENTITY_ALIAS);
    private static final Table rfbLocationTable = Table.aliased("rfb_location", "rfbLocation");

    public RfbEventRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RfbLocationRowMapper rfblocationMapper,
        RfbEventRowMapper rfbeventMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rfblocationMapper = rfblocationMapper;
        this.rfbeventMapper = rfbeventMapper;
    }

    @Override
    public Flux<RfbEvent> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RfbEvent> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RfbEvent> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RfbEventSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RfbLocationSqlHelper.getColumns(rfbLocationTable, "rfbLocation"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(rfbLocationTable)
            .on(Column.create("rfb_location_id", entityTable))
            .equals(Column.create("id", rfbLocationTable));

        String select = entityManager.createSelect(selectFrom, RfbEvent.class, pageable, criteria);
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
    public Flux<RfbEvent> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RfbEvent> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private RfbEvent process(Row row, RowMetadata metadata) {
        RfbEvent entity = rfbeventMapper.apply(row, "e");
        entity.setRfbLocation(rfblocationMapper.apply(row, "rfbLocation"));
        return entity;
    }

    @Override
    public <S extends RfbEvent> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RfbEvent> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RfbEvent with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(RfbEvent entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class RfbEventSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("event_date", table, columnPrefix + "_event_date"));
        columns.add(Column.aliased("event_code", table, columnPrefix + "_event_code"));

        columns.add(Column.aliased("rfb_location_id", table, columnPrefix + "_rfb_location_id"));
        return columns;
    }
}
