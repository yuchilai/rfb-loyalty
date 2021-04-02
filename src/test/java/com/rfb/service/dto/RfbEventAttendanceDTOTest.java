package com.rfb.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.rfb.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RfbEventAttendanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RfbEventAttendanceDTO.class);
        RfbEventAttendanceDTO rfbEventAttendanceDTO1 = new RfbEventAttendanceDTO();
        rfbEventAttendanceDTO1.setId(1L);
        RfbEventAttendanceDTO rfbEventAttendanceDTO2 = new RfbEventAttendanceDTO();
        assertThat(rfbEventAttendanceDTO1).isNotEqualTo(rfbEventAttendanceDTO2);
        rfbEventAttendanceDTO2.setId(rfbEventAttendanceDTO1.getId());
        assertThat(rfbEventAttendanceDTO1).isEqualTo(rfbEventAttendanceDTO2);
        rfbEventAttendanceDTO2.setId(2L);
        assertThat(rfbEventAttendanceDTO1).isNotEqualTo(rfbEventAttendanceDTO2);
        rfbEventAttendanceDTO1.setId(null);
        assertThat(rfbEventAttendanceDTO1).isNotEqualTo(rfbEventAttendanceDTO2);
    }
}
