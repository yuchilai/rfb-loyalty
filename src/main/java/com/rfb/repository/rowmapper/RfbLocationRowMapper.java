package com.rfb.repository.rowmapper;

import com.rfb.domain.RfbLocation;
import com.rfb.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RfbLocation}, with proper type conversions.
 */
@Service
public class RfbLocationRowMapper implements BiFunction<Row, String, RfbLocation> {

    private final ColumnConverter converter;

    public RfbLocationRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RfbLocation} stored in the database.
     */
    @Override
    public RfbLocation apply(Row row, String prefix) {
        RfbLocation entity = new RfbLocation();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLocationName(converter.fromRow(row, prefix + "_location_name", String.class));
        entity.setRunDayOfWeek(converter.fromRow(row, prefix + "_run_day_of_week", Integer.class));
        return entity;
    }
}
