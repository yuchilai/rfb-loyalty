package com.rfb.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RfbEventMapperTest {

    private RfbEventMapper rfbEventMapper;

    @BeforeEach
    public void setUp() {
        rfbEventMapper = new RfbEventMapperImpl();
    }
}
