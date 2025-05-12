package com.inrix.dto;

import com.inrix.model.Area;
import com.inrix.model.Zone;

import java.util.List;

public class AreaWithSplitZonesDTO {
    private Area area;
    private List<Zone> leftZones;
    private List<Zone> rightZones;

    public AreaWithSplitZonesDTO() {
    }

    public AreaWithSplitZonesDTO(Area area, List<Zone> leftZones, List<Zone> rightZones) {
        this.area = area;
        this.leftZones = leftZones;
        this.rightZones = rightZones;
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

    public void setArea(Area area) {
        this.area = area;
    }

    public void setLeftZones(List<Zone> leftZones) {
        this.leftZones = leftZones;
    }

    public void setRightZones(List<Zone> rightZones) {
        this.rightZones = rightZones;
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