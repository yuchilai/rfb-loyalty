package com.rfb.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rfb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RfbEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RfbEvent.class);
        RfbEvent rfbEvent1 = new RfbEvent();
        rfbEvent1.setId(1L);
        RfbEvent rfbEvent2 = new RfbEvent();
        rfbEvent2.setId(rfbEvent1.getId());
        assertThat(rfbEvent1).isEqualTo(rfbEvent2);
        rfbEvent2.setId(2L);
        assertThat(rfbEvent1).isNotEqualTo(rfbEvent2);
        rfbEvent1.setId(null);
        assertThat(rfbEvent1).isNotEqualTo(rfbEvent2);
    }
}
