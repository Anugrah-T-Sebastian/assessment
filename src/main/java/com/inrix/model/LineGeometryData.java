package com.inrix.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Coordinate;

import java.util.List;
import java.util.stream.Collectors;





import com.fasterxml.jackson.annotation.JsonIgnore;
import org.locationtech.jts.geom.Coordinate;
import java.util.List;
import java.util.stream.Collectors;

public class LineGeometryData {
    private String type;
    private List<List<Double>> coordinates; // Generic type to handle both formats

    @JsonIgnore
    public List<Coordinate> getLineCoordinates() {
        if ("LineString".equals(type)) {
            List<List<Double>> coords = (List<List<Double>>) coordinates;
            return coords.stream()
                    .map(coord -> new Coordinate(coord.get(0), coord.get(1)))
                    .collect(Collectors.toList());
        }
        throw new IllegalStateException("Not a LineString geometry");
    }

//    @JsonIgnore
//    public List<Coordinate> getPolygonCoordinates() {
//        if ("Polygon".equals(type)) {
//            List<List<List<Double>>> rings = (List<List<List<Double>>>) coordinates;
//            // Get first ring (outer boundary)
//            return rings.get(0).stream()
//                    .map(coord -> new Coordinate(coord.get(0), coord.get(1)))
//                    .collect(Collectors.toList());
//        }
//        throw new IllegalStateException("Not a Polygon geometry");
//    }



    // Standard getters
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