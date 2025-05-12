package com.inrix.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LineGeometryDataTest {

    @Test
    void testSetAndGetCoordinates() {
        // Arrange
        LineGeometryData lineGeometryData = new LineGeometryData();
        List<List<Double>> coordinates = new ArrayList<>();

        // Act
        lineGeometryData.setCoordinates(coordinates);

        // Assert
        assertEquals(coordinates, lineGeometryData.getCoordinates());
    }
}
