package com.rfb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A RfbLocation.
 */
@Table("rfb_location")
public class RfbLocation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("location_name")
    private String locationName;

    @Column("run_day_of_week")
    private Integer runDayOfWeek;

    @Transient
    @JsonIgnoreProperties(value = { "rfbEventAttendances", "rfbLocation" }, allowSetters = true)
    private Set<RfbEvent> rvbEvents = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RfbLocation id(Long id) {
        this.id = id;
        return this;
    }

    public String getLocationName() {
        return this.locationName;
    }

    public RfbLocation locationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Integer getRunDayOfWeek() {
        return this.runDayOfWeek;
    }

    public RfbLocation runDayOfWeek(Integer runDayOfWeek) {
        this.runDayOfWeek = runDayOfWeek;
        return this;
    }

    public void setRunDayOfWeek(Integer runDayOfWeek) {
        this.runDayOfWeek = runDayOfWeek;
    }

    public Set<RfbEvent> getRvbEvents() {
        return this.rvbEvents;
    }

    public RfbLocation rvbEvents(Set<RfbEvent> rfbEvents) {
        this.setRvbEvents(rfbEvents);
        return this;
    }

    public RfbLocation addRvbEvent(RfbEvent rfbEvent) {
        this.rvbEvents.add(rfbEvent);
        rfbEvent.setRfbLocation(this);
        return this;
    }

    public RfbLocation removeRvbEvent(RfbEvent rfbEvent) {
        this.rvbEvents.remove(rfbEvent);
        rfbEvent.setRfbLocation(null);
        return this;
    }

    public void setRvbEvents(Set<RfbEvent> rfbEvents) {
        if (this.rvbEvents != null) {
            this.rvbEvents.forEach(i -> i.setRfbLocation(null));
        }
        if (rfbEvents != null) {
            rfbEvents.forEach(i -> i.setRfbLocation(this));
        }
        this.rvbEvents = rfbEvents;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RfbLocation)) {
            return false;
        }
        return id != null && id.equals(((RfbLocation) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RfbLocation{" +
            "id=" + getId() +
            ", locationName='" + getLocationName() + "'" +
            ", runDayOfWeek=" + getRunDayOfWeek() +
            "}";
    }
}
