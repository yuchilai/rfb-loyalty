package com.rfb.repository.rowmapper;

import com.rfb.domain.RfbEventAttendance;
import com.rfb.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link RfbEventAttendance}, with proper type conversions.
 */
@Service
public class RfbEventAttendanceRowMapper implements BiFunction<Row, String, RfbEventAttendance> {

    private final ColumnConverter converter;

    public RfbEventAttendanceRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link RfbEventAttendance} stored in the database.
     */
    @Override
    public RfbEventAttendance apply(Row row, String prefix) {
        RfbEventAttendance entity = new RfbEventAttendance();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setAttendanceDate(converter.fromRow(row, prefix + "_attendance_date", LocalDate.class));
        entity.setRfbEventId(converter.fromRow(row, prefix + "_rfb_event_id", Long.class));
        entity.setRfbUserId(converter.fromRow(row, prefix + "_rfb_user_id", Long.class));
        return entity;
    }
}
