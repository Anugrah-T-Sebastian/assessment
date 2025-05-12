package com.inrix.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AreaTest {

    @Test
    void testGetId() {
        // Arrange
        Area area = new Area();
        area.setCurbAreaId(123);

        // Act
        String id = area.getId();

        // Assert
        assertEquals("123", id);
    }

    @Test
    void testToString() {
        // Arrange
        Area area = new Area();
        area.setCurbAreaId(123);

        // Act
        String result = area.toString();

        // Assert
        assertTrue(result.contains("123"));
    }
}
