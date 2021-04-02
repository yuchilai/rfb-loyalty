package com.rfb.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.rfb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RfbLocationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RfbLocationDTO.class);
        RfbLocationDTO rfbLocationDTO1 = new RfbLocationDTO();
        rfbLocationDTO1.setId(1L);
        RfbLocationDTO rfbLocationDTO2 = new RfbLocationDTO();
        assertThat(rfbLocationDTO1).isNotEqualTo(rfbLocationDTO2);
        rfbLocationDTO2.setId(rfbLocationDTO1.getId());
        assertThat(rfbLocationDTO1).isEqualTo(rfbLocationDTO2);
        rfbLocationDTO2.setId(2L);
        assertThat(rfbLocationDTO1).isNotEqualTo(rfbLocationDTO2);
        rfbLocationDTO1.setId(null);
        assertThat(rfbLocationDTO1).isNotEqualTo(rfbLocationDTO2);
    }
}
