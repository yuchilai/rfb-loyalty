package com.rfb.repository.rowmapper;

import com.rfb.domain.RfbUser;
import com.rfb.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RfbUser}, with proper type conversions.
 */
@Service
public class RfbUserRowMapper implements BiFunction<Row, String, RfbUser> {

    private final ColumnConverter converter;

    public RfbUserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RfbUser} stored in the database.
     */
    @Override
    public RfbUser apply(Row row, String prefix) {
        RfbUser entity = new RfbUser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUsername(converter.fromRow(row, prefix + "_username", String.class));
        entity.setHomeLocationId(converter.fromRow(row, prefix + "_home_location_id", Long.class));
        return entity;
    }
}
