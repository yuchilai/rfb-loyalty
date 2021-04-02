package com.rfb.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.rfb.domain.RfbUser;
import com.rfb.repository.rowmapper.RfbLocationRowMapper;
import com.rfb.repository.rowmapper.RfbUserRowMapper;
import com.rfb.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
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
 * Spring Data SQL reactive custom repository implementation for the RfbUser entity.
 */
@SuppressWarnings("unused")
class RfbUserRepositoryInternalImpl implements RfbUserRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final RfbLocationRowMapper rfblocationMapper;
    private final RfbUserRowMapper rfbuserMapper;

    private static final Table entityTable = Table.aliased("rfb_user", EntityManager.ENTITY_ALIAS);
    private static final Table homeLocationTable = Table.aliased("rfb_location", "homeLocation");

    public RfbUserRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        RfbLocationRowMapper rfblocationMapper,
        RfbUserRowMapper rfbuserMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.rfblocationMapper = rfblocationMapper;
        this.rfbuserMapper = rfbuserMapper;
    }

    @Override
    public Flux<RfbUser> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<RfbUser> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<RfbUser> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = RfbUserSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(RfbLocationSqlHelper.getColumns(homeLocationTable, "homeLocation"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(homeLocationTable)
            .on(Column.create("home_location_id", entityTable))
            .equals(Column.create("id", homeLocationTable));

        String select = entityManager.createSelect(selectFrom, RfbUser.class, pageable, criteria);
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
    public Flux<RfbUser> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<RfbUser> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private RfbUser process(Row row, RowMetadata metadata) {
        RfbUser entity = rfbuserMapper.apply(row, "e");
        entity.setHomeLocation(rfblocationMapper.apply(row, "homeLocation"));
        return entity;
    }

    @Override
    public <S extends RfbUser> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends RfbUser> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update RfbUser with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(RfbUser entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class RfbUserSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("username", table, columnPrefix + "_username"));

        columns.add(Column.aliased("home_location_id", table, columnPrefix + "_home_location_id"));
        return columns;
    }
}
