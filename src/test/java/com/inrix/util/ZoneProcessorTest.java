package com.inrix.util;

import com.inrix.dto.AreaWithSplitZonesDTO;
import com.inrix.model.Area;
import com.inrix.model.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ZoneProcessorTest {

    private ZoneProcessor zoneProcessor;

    @BeforeEach
    void setUp() {
        zoneProcessor = new ZoneProcessor();
    }

    // Test data providers
    static Stream<Arguments> zoneSideProvider() {
        return Stream.of(
                Arguments.of("left", "left"),
                Arguments.of("LEFT", "left"),
                Arguments.of("right", "right"),
                Arguments.of("RIGHT", "right"),
                Arguments.of("unknown", "unknown"),
                Arguments.of("", "unknown"),
                Arguments.of(null, "unknown")
        );
    }

    static Stream<Arguments> multipleZonesProvider() {
        return Stream.of(
                Arguments.of(
                        Arrays.asList("zone1", "zone2", "zone3", "zone4"),
                        Arrays.asList(
                                new Zone.LocationReference("left"),
                                new Zone.LocationReference("right"),
                                new Zone.LocationReference("LEFT"),
                                new Zone.LocationReference("RIGHT")
                        ),
                        2, 2
                ),
                Arguments.of(
                        Arrays.asList("zone1", "zone2", "zone3"),
                        Arrays.asList(
                                new Zone.LocationReference("left"),
                                new Zone.LocationReference("left"),
                                new Zone.LocationReference("right")
                        ),
                        2, 1
                )
        );
    }

    // Test cases
    @Test
    void testProcessAreasWithSplitZones_HappyPath() {
        // Arrange
        Map<String, Area> areas = new HashMap<>();
        Area area = new Area();
        area.setCurbAreaId(1);
        area.setCurbZoneIds(Arrays.asList("zone1", "zone2"));
        areas.put("1", area);

        List<Zone> zones = new ArrayList<>();
        Zone zone1 = new Zone();
        zone1.setCurbZoneId("zone1");
        zone1.setLocationReferences(Collections.singletonList(new Zone.LocationReference("left")));
        zones.add(zone1);

        Zone zone2 = new Zone();
        zone2.setCurbZoneId("zone2");
        zone2.setLocationReferences(Collections.singletonList(new Zone.LocationReference("right")));
        zones.add(zone2);

        // Act
        List<AreaWithSplitZonesDTO> result = zoneProcessor.processAreasWithSplitZones(areas, zones);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        AreaWithSplitZonesDTO dto = result.get(0);
        assertEquals(1, dto.getLeftZones().size());
        assertEquals(1, dto.getRightZones().size());
        assertEquals("zone1", dto.getLeftZones().get(0).getCurbZoneId());
        assertEquals("zone2", dto.getRightZones().get(0).getCurbZoneId());
    }

    @Test
    void testProcessAreasWithSplitZones_EmptyAreas() {
        // Arrange
        Map<String, Area> areas = new HashMap<>();
        List<Zone> zones = new ArrayList<>();

        // Act
        List<AreaWithSplitZonesDTO> result = zoneProcessor.processAreasWithSplitZones(areas, zones);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testProcessAreasWithSplitZones_NullInputs() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> zoneProcessor.processAreasWithSplitZones(null, new ArrayList<>()));
        assertThrows(NullPointerException.class, () -> zoneProcessor.processAreasWithSplitZones(new HashMap<>(), null));
    }

    @Test
    void testProcessAreasWithSplitZones_AreaWithNoZones() {
        // Arrange
        Map<String, Area> areas = new HashMap<>();
        Area area = new Area();
        area.setCurbAreaId(1);
        area.setCurbZoneIds(Collections.emptyList());
        areas.put("1", area);

        List<Zone> zones = new ArrayList<>();

        // Act
        List<AreaWithSplitZonesDTO> result = zoneProcessor.processAreasWithSplitZones(areas, zones);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        AreaWithSplitZonesDTO dto = result.get(0);
        assertTrue(dto.getLeftZones().isEmpty());
        assertTrue(dto.getRightZones().isEmpty());
    }

    @Test
    void testProcessAreasWithSplitZones_ZoneNotInMap() {
        // Arrange
        Map<String, Area> areas = new HashMap<>();
        Area area = new Area();
        area.setCurbAreaId(1);
        area.setCurbZoneIds(Arrays.asList("zone1", "nonexistent"));
        areas.put("1", area);

        List<Zone> zones = new ArrayList<>();
        Zone zone1 = new Zone();
        zone1.setCurbZoneId("zone1");
        zone1.setLocationReferences(Collections.singletonList(new Zone.LocationReference("left")));
        zones.add(zone1);

        // Act
        List<AreaWithSplitZonesDTO> result = zoneProcessor.processAreasWithSplitZones(areas, zones);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        AreaWithSplitZonesDTO dto = result.get(0);
        assertEquals(1, dto.getLeftZones().size());
        assertTrue(dto.getRightZones().isEmpty());
    }

    @Test
    void testProcessAreasWithSplitZones_MultipleAreas() {
        // Arrange
        Map<String, Area> areas = new HashMap<>();

        Area area1 = new Area();
        area1.setCurbAreaId(1);
        area1.setCurbZoneIds(Arrays.asList("zone1", "zone2"));
        areas.put("1", area1);

        Area area2 = new Area();
        area2.setCurbAreaId(2);
        area2.setCurbZoneIds(Arrays.asList("zone3", "zone4"));
        areas.put("2", area2);

        List<Zone> zones = new ArrayList<>();
        Zone zone1 = new Zone();
        zone1.setCurbZoneId("zone1");
        zone1.setLocationReferences(Collections.singletonList(new Zone.LocationReference("left")));
        zones.add(zone1);

        Zone zone2 = new Zone();
        zone2.setCurbZoneId("zone2");
        zone2.setLocationReferences(Collections.singletonList(new Zone.LocationReference("right")));
        zones.add(zone2);

        Zone zone3 = new Zone();
        zone3.setCurbZoneId("zone3");
        zone3.setLocationReferences(Collections.singletonList(new Zone.LocationReference("left")));
        zones.add(zone3);

        Zone zone4 = new Zone();
        zone4.setCurbZoneId("zone4");
        zone4.setLocationReferences(Collections.singletonList(new Zone.LocationReference("right")));
        zones.add(zone4);

        // Act
        List<AreaWithSplitZonesDTO> result = zoneProcessor.processAreasWithSplitZones(areas, zones);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify first area
        AreaWithSplitZonesDTO dto1 = result.get(0);
        assertEquals(1, dto1.getLeftZones().size());
        assertEquals(1, dto1.getRightZones().size());

        // Verify second area
        AreaWithSplitZonesDTO dto2 = result.get(1);
        assertEquals(1, dto2.getLeftZones().size());
        assertEquals(1, dto2.getRightZones().size());
    }

    @ParameterizedTest
    @MethodSource("zoneSideProvider")
    void testDetermineZoneSide_WithReflection(String inputSide, String expectedSide)
            throws Exception {
        // Arrange
        Zone zone = new Zone();
        if (inputSide != null) {
            zone.setLocationReferences(List.of(new Zone.LocationReference(inputSide)));
        } else {
            zone.setLocationReferences(List.of(new Zone.LocationReference(null)));
        }

        // Get the private method via reflection
        Method method = ZoneProcessor.class.getDeclaredMethod("determineZoneSide", Zone.class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(zoneProcessor, zone);

        // Assert
        assertEquals(expectedSide, result);
    }

    @Test
    void testDetermineZoneSide_WithMultipleReferences_WithReflection() throws Exception {
        // Arrange
        Zone zone = new Zone();
        zone.setLocationReferences(List.of(
                new Zone.LocationReference(null),
                new Zone.LocationReference("right")
        ));

        // Get the private method via reflection
        Method method = ZoneProcessor.class.getDeclaredMethod("determineZoneSide", Zone.class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(zoneProcessor, zone);

        // Assert
        assertEquals("right", result);
    }

    @ParameterizedTest
    @MethodSource("multipleZonesProvider")
    void testSplitZonesBySide_MultipleZones(List<String> zoneIds,
                                            List<Zone.LocationReference> references,
                                            int expectedLeft, int expectedRight)
            throws Exception {

        // Arrange
        Area area = new Area();
        area.setCurbZoneIds(zoneIds);

        Map<String, Zone> zoneMap = new HashMap<>();
        for (int i = 0; i < zoneIds.size(); i++) {
            Zone zone = new Zone();
            zone.setCurbZoneId(zoneIds.get(i));
            zone.setLocationReferences(Collections.singletonList(references.get(i)));
            zoneMap.put(zoneIds.get(i), zone);
        }

        // Use reflection to access the private method
        Method method = ZoneProcessor.class.getDeclaredMethod(
                "splitZonesBySide",
                Area.class,
                Map.class
        );
        method.setAccessible(true); // Make the private method accessible

        // Act
        @SuppressWarnings("unchecked")
        Map<String, List<Zone>> result = (Map<String, List<Zone>>)
                method.invoke(zoneProcessor, area, zoneMap);

        // Assert
        assertNotNull(result);
        assertEquals(expectedLeft, result.get("left").size());
        assertEquals(expectedRight, result.get("right").size());
    }

    @Test
    void testCreateZoneMap_WithReflection() throws Exception {
        // Arrange
        List<Zone> zones = new ArrayList<>();
        Zone zone1 = new Zone();
        zone1.setCurbZoneId("zone1");
        zones.add(zone1);

        // Get the private method via reflection
        Method method = ZoneProcessor.class.getDeclaredMethod("createZoneMap", List.class);
        method.setAccessible(true);

        // Act
        @SuppressWarnings("unchecked")
        Map<String, Zone> result = (Map<String, Zone>) method.invoke(zoneProcessor, zones);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("zone1", result.get("zone1").getCurbZoneId());
    }

    @Test
    void testCreateZoneMap_EmptyList() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        List<Zone> zones = new ArrayList<>();

        // Act
        Method method = ZoneProcessor.class.getDeclaredMethod("createZoneMap", List.class);
        method.setAccessible(true);
        Map<String, Zone> result = (Map<String, Zone>) method.invoke(zoneProcessor, zones);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateAreaWithSplitZones_WithReflection() throws Exception {
        // Arrange
        Area area = new Area();
        area.setCurbAreaId(1);
        area.setCurbZoneIds(List.of("zone1"));

        Map<String, Zone> zoneMap = new HashMap<>();
        Zone zone = new Zone();
        zone.setCurbZoneId("zone1");
        zone.setLocationReferences(List.of(new Zone.LocationReference("left")));
        zoneMap.put("zone1", zone);

        // Get the private method via reflection
        Method method = ZoneProcessor.class.getDeclaredMethod("createAreaWithSplitZones", Area.class, Map.class);
        method.setAccessible(true);

        // Act
        AreaWithSplitZonesDTO result = (AreaWithSplitZonesDTO) method.invoke(zoneProcessor, area, zoneMap);

        // Assert
        assertNotNull(result);
        assertEquals(area, result.getArea());
        assertEquals(1, result.getLeftZones().size());
        assertTrue(result.getRightZones().isEmpty());
    }
}