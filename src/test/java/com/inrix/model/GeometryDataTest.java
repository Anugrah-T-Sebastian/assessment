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

        // Act
        geometryData.setCoordinates(coordinates);

        // Assert
        assertEquals(coordinates, geometryData.getCoordinates());
    }
}
