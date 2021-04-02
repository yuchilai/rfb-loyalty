package com.rfb.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.rfb.domain.RfbEvent} entity.
 */
public class RfbEventDTO implements Serializable {

    private Long id;

    private LocalDate eventDate;

    private String eventCode;

    private RfbLocationDTO rfbLocation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public RfbLocationDTO getRfbLocation() {
        return rfbLocation;
    }

    public void setRfbLocation(RfbLocationDTO rfbLocation) {
        this.rfbLocation = rfbLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RfbEventDTO)) {
            return false;
        }

        RfbEventDTO rfbEventDTO = (RfbEventDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, rfbEventDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RfbEventDTO{" +
            "id=" + getId() +
            ", eventDate='" + getEventDate() + "'" +
            ", eventCode='" + getEventCode() + "'" +
            ", rfbLocation=" + getRfbLocation() +
            "}";
    }
}
