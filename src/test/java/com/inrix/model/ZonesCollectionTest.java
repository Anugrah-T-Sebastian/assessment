package com.inrix.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZonesCollectionTest {

    @Test
    void testSetAndGetZones() {
        // Arrange
        ZonesCollection zonesCollection = new ZonesCollection();
        List<Zone> zones = new ArrayList<>();

        // Act
        zonesCollection.setZones(zones);

        // Assert
        assertEquals(zones, zonesCollection.getZones());
    }
}
