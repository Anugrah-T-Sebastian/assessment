package com.inrix.model;

import java.util.List;

public class GeometryData {
    private String type;
    private List<List<List<Double>>> coordinates;

    public String getType() { return type; }

    public List<List<Double>> getCoordinates() {
        return coordinates.get(0);
    }

    public void setCoordinates(List<List<List<Double>>> coordinates) {
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