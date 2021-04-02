package com.rfb.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rfb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RfbUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RfbUser.class);
        RfbUser rfbUser1 = new RfbUser();
        rfbUser1.setId(1L);
        RfbUser rfbUser2 = new RfbUser();
        rfbUser2.setId(rfbUser1.getId());
        assertThat(rfbUser1).isEqualTo(rfbUser2);
        rfbUser2.setId(2L);
        assertThat(rfbUser1).isNotEqualTo(rfbUser2);
        rfbUser1.setId(null);
        assertThat(rfbUser1).isNotEqualTo(rfbUser2);
    }
}
