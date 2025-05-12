package com.inrix.dto;

import com.inrix.model.Area;
import com.inrix.model.Zone;

import java.util.List;

public final class AreaWithZonesDTO {
    private final Area area;
    private final List<Zone> zones;

    public AreaWithZonesDTO(Area area, List<Zone> zones) {
        this.area = area;
        this.zones = zones;
    }

    // Getters
    public Area getArea() {
        return area;
    }

    public List<Zone> getZones() {
        return zones;
    }

    // Optional: toString(), equals(), hashCode() for better debugging
    @Override
    public String toString() {
        return "AreaWithZonesDTO{" +
                "area=" + area +
                ", zones=" + zones +
                '}';
    }
}