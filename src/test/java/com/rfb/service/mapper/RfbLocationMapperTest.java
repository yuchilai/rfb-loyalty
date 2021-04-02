package com.rfb.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RfbLocationMapperTest {

    private RfbLocationMapper rfbLocationMapper;

    @BeforeEach
    public void setUp() {
        rfbLocationMapper = new RfbLocationMapperImpl();
    }
}
