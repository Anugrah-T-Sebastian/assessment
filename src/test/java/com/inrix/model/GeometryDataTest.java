package com.inrix.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

class GeometryDataTest {

    @Test
    void testSetAndGetCoordinates() {
        // Arrange
        GeometryData geometryData = new GeometryData();
        List<List<List<Double>>> coordinates = new ArrayList<>();

        // Create a nested list structure with at least one element
        List<List<Double>> outerList = new ArrayList<>();
        List<Double> innerList = new ArrayList<>();
        innerList.add(1.0);
        innerList.add(2.0);
        outerList.add(innerList);
        coordinates.add(outerList);

        // Act
        geometryData.setCoordinates(coordinates);

        // Assert
        assertEquals(outerList, geometryData.getCoordinates());
    }
}
