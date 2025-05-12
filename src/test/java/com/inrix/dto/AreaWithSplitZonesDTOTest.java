package com.inrix.dto;

import com.inrix.model.Area;
import com.inrix.model.Zone;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AreaWithSplitZonesDTOTest {

    @Test
    void testConstructorAndGetters() {
        // Arrange
        Area area = new Area();
        List<Zone> leftZones = new ArrayList<>();
        List<Zone> rightZones = new ArrayList<>();

        // Act
        AreaWithSplitZonesDTO dto = new AreaWithSplitZonesDTO(area, leftZones, rightZones);

        // Assert
        assertEquals(area, dto.getArea());
        assertEquals(leftZones, dto.getLeftZones());
        assertEquals(rightZones, dto.getRightZones());
    }
}
