package com.inrix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;
import java.util.stream.Collectors;

public class LineGeometryData {
    private String type;
    private List<List<Double>> coordinates; // Generic type to handle both formats

    public String getType() { return type; }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return "GeometryData{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}