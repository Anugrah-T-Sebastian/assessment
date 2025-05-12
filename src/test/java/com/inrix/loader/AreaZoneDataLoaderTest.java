package com.inrix.loader;

import com.inrix.model.Area;
import com.inrix.model.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AreaZoneDataLoaderTest {

    private AreaZoneDataLoader dataLoader;

    @BeforeEach
    void setUp() {
        dataLoader = new AreaZoneDataLoader();
    }

    @Test
    void testLoadAreas() throws Exception {
        // Arrange
        String filePath = "data/areas.json";

        // Act
        Map<String, Area> areas = dataLoader.loadAreas(filePath);

        // Assert
        assertNotNull(areas);
        assertFalse(areas.isEmpty());
    }

    @Test
    void testLoadZones() throws Exception {
        // Arrange
        String filePath = "data/zones.json";

        // Act
        List<Zone> zones = dataLoader.loadZones(filePath);

        // Assert
        assertNotNull(zones);
        assertFalse(zones.isEmpty());
    }
}
