package com.inrix.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ZonesCollection {
    @JsonProperty("zones")
    private List<Zone> zones;
    public List<Zone> getZones() { return zones; }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }
}