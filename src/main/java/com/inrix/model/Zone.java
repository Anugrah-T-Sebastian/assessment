package com.inrix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Zone {
    @JsonProperty("curb_zone_id")
    private String curbZoneId;
    @JsonProperty("geometry")
    private LineGeometryData geometry;
    @JsonProperty("published_date")
    private Long publishedDate;
    @JsonProperty("last_updated_date")
    private Long lastUpdatedDate;
    @JsonProperty("start_date")
    private Long startDate;

    @JsonProperty("location_references")
    private List<LocationReference> locationReferences;
    @JsonProperty("street_name")
    private String streetName;
    @JsonProperty("cross_street_start_name")
    private String crossStreetStartName;
    @JsonProperty("cross_street_end_name")
    private String crossStreetEndName;
    @JsonProperty("curb_policy_ids")
    private List<String> curbPolicyIds;
    @JsonProperty("parking_angle")
    private String parkingAngle;
    @JsonProperty("num_spaces")
    private Integer numSpaces;

    public static class Id {
        @JsonProperty("$oid")
        private String oid;
        public String getOid() { return oid; }
    }

    public static class LocationReference {
        @JsonProperty("source")
        private String source;
        @JsonProperty("ref_id")
        private String refId;
        @JsonProperty("start")
        private Integer start;
        @JsonProperty("end")
        private Integer end;
        @JsonProperty("side")
        private String side;

        public LocationReference() {
        }

        public LocationReference(String side) {
            this.side = side;
        }

        public String getSide() { return side; }

        public void setSide(String side) {
            this.side = side;
        }

        @Override
        public String toString() {
            return "LocationReference{" +
                    "source='" + source + '\'' +
                    ", refId='" + refId + '\'' +
                    ", start=" + start +
                    ", end=" + end +
                    ", side='" + side + '\'' +
                    "}\n";
        }
    }

    // Getters
    public String getCurbZoneId() { return curbZoneId; }
    public LineGeometryData getGeometry() { return geometry; }
    public String getParkingAngle() { return parkingAngle; }
    public List<LocationReference> getLocationReferences() {
        return locationReferences;
    }

    // Setter


    public void setCurbZoneId(String curbZoneId) {
        this.curbZoneId = curbZoneId;
    }

    public void setLocationReferences(List<LocationReference> locationReferences) {
        this.locationReferences = locationReferences;
    }

    @Override
    public String toString() {
        return "Zone{" +
                "curbZoneId='" + curbZoneId + '\'' +
                ", geometry=" + geometry +
                ", publishedDate=" + publishedDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", startDate=" + startDate +
                ", locationReferences=" + locationReferences +
                ", streetName='" + streetName + '\'' +
                ", crossStreetStartName='" + crossStreetStartName + '\'' +
                ", crossStreetEndName='" + crossStreetEndName + '\'' +
                ", curbPolicyIds=" + curbPolicyIds +
                ", parkingAngle='" + parkingAngle + '\'' +
                ", numSpaces=" + numSpaces +
                "}\n";
    }
}
