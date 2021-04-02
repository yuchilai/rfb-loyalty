package com.rfb.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rfb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RfbLocationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RfbLocation.class);
        RfbLocation rfbLocation1 = new RfbLocation();
        rfbLocation1.setId(1L);
        RfbLocation rfbLocation2 = new RfbLocation();
        rfbLocation2.setId(rfbLocation1.getId());
        assertThat(rfbLocation1).isEqualTo(rfbLocation2);
        rfbLocation2.setId(2L);
        assertThat(rfbLocation1).isNotEqualTo(rfbLocation2);
        rfbLocation1.setId(null);
        assertThat(rfbLocation1).isNotEqualTo(rfbLocation2);
    }
}
