package com.rfb.repository.rowmapper;

import com.rfb.domain.RfbEvent;
import com.rfb.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RfbEvent}, with proper type conversions.
 */
@Service
public class RfbEventRowMapper implements BiFunction<Row, String, RfbEvent> {

    private final ColumnConverter converter;

    public RfbEventRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RfbEvent} stored in the database.
     */
    @Override
    public RfbEvent apply(Row row, String prefix) {
        RfbEvent entity = new RfbEvent();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setEventDate(converter.fromRow(row, prefix + "_event_date", LocalDate.class));
        entity.setEventCode(converter.fromRow(row, prefix + "_event_code", String.class));
        entity.setRfbLocationId(converter.fromRow(row, prefix + "_rfb_location_id", Long.class));
        return entity;
    }
}
