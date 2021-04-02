package com.rfb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RfbEventAttendance.
 */
@Table("rfb_event_attendance")
public class RfbEventAttendance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("attendance_date")
    private LocalDate attendanceDate;

    @JsonIgnoreProperties(value = { "rfbEventAttendances", "rfbLocation" }, allowSetters = true)
    @Transient
    private RfbEvent rfbEvent;

    @Column("rfb_event_id")
    private Long rfbEventId;

    @JsonIgnoreProperties(value = { "homeLocation", "rfbEventAttendances" }, allowSetters = true)
    @Transient
    private RfbUser rfbUser;

    @Column("rfb_user_id")
    private Long rfbUserId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RfbEventAttendance id(Long id) {
        this.id = id;
        return this;
    }

    public LocalDate getAttendanceDate() {
        return this.attendanceDate;
    }

    public RfbEventAttendance attendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
        return this;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public RfbEvent getRfbEvent() {
        return this.rfbEvent;
    }

    public RfbEventAttendance rfbEvent(RfbEvent rfbEvent) {
        this.setRfbEvent(rfbEvent);
        this.rfbEventId = rfbEvent != null ? rfbEvent.getId() : null;
        return this;
    }

    public void setRfbEvent(RfbEvent rfbEvent) {
        this.rfbEvent = rfbEvent;
        this.rfbEventId = rfbEvent != null ? rfbEvent.getId() : null;
    }

    public Long getRfbEventId() {
        return this.rfbEventId;
    }

    public void setRfbEventId(Long rfbEvent) {
        this.rfbEventId = rfbEvent;
    }

    public RfbUser getRfbUser() {
        return this.rfbUser;
    }

    public RfbEventAttendance rfbUser(RfbUser rfbUser) {
        this.setRfbUser(rfbUser);
        this.rfbUserId = rfbUser != null ? rfbUser.getId() : null;
        return this;
    }

    public void setRfbUser(RfbUser rfbUser) {
        this.rfbUser = rfbUser;
        this.rfbUserId = rfbUser != null ? rfbUser.getId() : null;
    }

    public Long getRfbUserId() {
        return this.rfbUserId;
    }

    public void setRfbUserId(Long rfbUser) {
        this.rfbUserId = rfbUser;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RfbEventAttendance)) {
            return false;
        }
        return id != null && id.equals(((RfbEventAttendance) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RfbEventAttendance{" +
            "id=" + getId() +
            ", attendanceDate='" + getAttendanceDate() + "'" +
            "}";
    }
}
