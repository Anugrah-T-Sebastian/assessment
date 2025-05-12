package com.inrix.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Area {
    @JsonProperty("curb_area_id")
    private Integer curbAreaId;
    @JsonProperty("geometry")
    private GeometryData geometry;
    @JsonProperty("curb_zone_ids")
    private List<String> curbZoneIds;

    public String getId() {
        return this.curbAreaId.toString();
    }


    public static class AreaCollection {
        @JsonProperty("areas")
        private List<Area> areas;

        public List<Area> getAreas() { return areas; }
    }

    // Getters
    public Integer getCurbAreaId() { return curbAreaId; }
    public GeometryData getGeometry() { return geometry; }
    public List<String> getCurbZoneIds() { return curbZoneIds; }

    public void setCurbAreaId(Integer curbAreaId) {
        this.curbAreaId = curbAreaId;
    }

    // Setter
    public void setCurbAreaId(int i) {
        this.curbAreaId = i;
    }
    public void setCurbZoneIds(List<String> curbZoneIds) {
        this.curbZoneIds = curbZoneIds;
    }
    public void setGeometry(GeometryData geometry) {
        this.geometry = geometry;
    }

    @Override
    public String toString() {
        return "Area{" +
                "curbAreaId=" + curbAreaId +
                ", geometry=" + geometry +
                ", curbZoneIds=" + curbZoneIds +
                '}';
    }
}
