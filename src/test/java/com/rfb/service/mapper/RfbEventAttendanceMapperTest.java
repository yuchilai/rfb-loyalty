package com.rfb.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RfbEventAttendanceMapperTest {

    private RfbEventAttendanceMapper rfbEventAttendanceMapper;

    @BeforeEach
    public void setUp() {
        rfbEventAttendanceMapper = new RfbEventAttendanceMapperImpl();
    }
}
