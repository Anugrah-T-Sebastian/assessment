package com.inrix.dto;

import com.inrix.model.Area;
import com.inrix.model.Zone;

import java.util.ArrayList;
import java.util.List;

public class AreaWithSplitZonesDTO {
    private final Area area;
    private final List<Zone> leftZones;
    private final List<Zone> rightZones;

    public AreaWithSplitZonesDTO(Area area, List<Zone> leftZones, List<Zone> rightZones) {
        this.area = area;
        this.leftZones = leftZones != null ? leftZones : new ArrayList<>();
        this.rightZones = rightZones != null ? rightZones : new ArrayList<>();
    }

    // Getters
    public Area getArea() {
        return area;
    }

    public List<Zone> getLeftZones() {
        return leftZones;
    }

    public List<Zone> getRightZones() {
        return rightZones;
    }

    @Override
    public String toString() {
        return "AreaWithSplitZonesDTO{" +
                "area=" + area +
                ", leftZones=" + leftZones.size() +
                ", rightZones=" + rightZones.size() +
                '}';
    }
}