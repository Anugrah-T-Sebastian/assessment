package com.inrix.model;

import java.util.List;

public class LineGeometryData {
    private String type;
    private List<List<Double>> coordinates; // Generic type to handle both formats

    public String getType() { return type; }

    public List<List<Double>> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<List<Double>> coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "GeometryData{" +
                "type='" + type + '\'' +
                ", coordinates=" + coordinates +
                '}';
    }
}