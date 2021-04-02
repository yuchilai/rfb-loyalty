package com.rfb.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RfbUserMapperTest {

    private RfbUserMapper rfbUserMapper;

    @BeforeEach
    public void setUp() {
        rfbUserMapper = new RfbUserMapperImpl();
    }
}
