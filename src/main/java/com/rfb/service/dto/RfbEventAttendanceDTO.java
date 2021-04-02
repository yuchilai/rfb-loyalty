package com.rfb.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.rfb.domain.RfbEventAttendance} entity.
 */
public class RfbEventAttendanceDTO implements Serializable {

    private Long id;

    private LocalDate attendanceDate;

    private RfbEventDTO rfbEvent;

    private RfbUserDTO rfbUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public RfbEventDTO getRfbEvent() {
        return rfbEvent;
    }

    public void setRfbEvent(RfbEventDTO rfbEvent) {
        this.rfbEvent = rfbEvent;
    }

    public RfbUserDTO getRfbUser() {
        return rfbUser;
    }

    public void setRfbUser(RfbUserDTO rfbUser) {
        this.rfbUser = rfbUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RfbEventAttendanceDTO)) {
            return false;
        }

        RfbEventAttendanceDTO rfbEventAttendanceDTO = (RfbEventAttendanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rfbEventAttendanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RfbEventAttendanceDTO{" +
            "id=" + getId() +
            ", attendanceDate='" + getAttendanceDate() + "'" +
            ", rfbEvent=" + getRfbEvent() +
            ", rfbUser=" + getRfbUser() +
            "}";
    }
}
