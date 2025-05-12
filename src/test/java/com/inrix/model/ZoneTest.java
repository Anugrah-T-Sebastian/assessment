package com.inrix.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoneTest {

    @Test
    void testSetAndGetCurbZoneId() {
        // Arrange
        Zone zone = new Zone();
        String curbZoneId = "zone1";

        // Act
        zone.setCurbZoneId(curbZoneId);

        // Assert
        assertEquals(curbZoneId, zone.getCurbZoneId());
    }
}
