package com.inrix.util;

import com.inrix.dto.AreaWithSplitZonesDTO;
import com.inrix.model.Area;
import com.inrix.model.GeometryData;
import com.inrix.model.LineGeometryData;
import com.inrix.model.Zone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GeometryConverterTest {
    private GeometryConverter geometryConverter;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        geometryConverter = new GeometryConverter();
        geometryFactory = new GeometryFactory();
    }

    @Test
    void testConvertToGeometries() {
        List<AreaWithSplitZonesDTO> areas = new ArrayList<>();
        areas.add(createTestAreaWithZones());

        List<List<Geometry>> result = geometryConverter.convertToGeometries(areas);

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).size());
        assertTrue(result.get(0).get(0) instanceof Polygon);
        assertTrue(result.get(0).get(1) instanceof Geometry);
        assertTrue(result.get(0).get(2) instanceof Geometry);
    }

    @Test
    void testConvertAreaToGeometries() throws Exception {
        AreaWithSplitZonesDTO dto = createTestAreaWithZones();

        Method method = GeometryConverter.class.getDeclaredMethod("convertAreaToGeometries", AreaWithSplitZonesDTO.class);
        method.setAccessible(true);
        List<Geometry> result = (List<Geometry>) method.invoke(geometryConverter, dto);

        assertEquals(3, result.size());
        assertTrue(result.get(0) instanceof Polygon);
        assertTrue(result.get(1) instanceof Geometry);
        assertTrue(result.get(2) instanceof Geometry);
    }

    @Test
    void testCreatePolygonFromCoordinates() throws Exception {
        List<List<Double>> coordinates = Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(1.0, 0.0),
                Arrays.asList(1.0, 1.0),
                Arrays.asList(0.0, 1.0),
                Arrays.asList(0.0, 0.0)
        );

        Method method = GeometryConverter.class.getDeclaredMethod("createPolygonFromCoordinates", List.class);
        method.setAccessible(true);
        Polygon result = (Polygon) method.invoke(geometryConverter, coordinates);

        assertNotNull(result);
        assertEquals(5, result.getCoordinates().length);
        assertEquals(0.0, result.getCoordinates()[0].x);
        assertEquals(0.0, result.getCoordinates()[0].y);
    }

    @Test
    void testMergeZoneCoordinates() throws Exception {
        List<Zone> zones = new ArrayList<>();
        zones.add(createTestZone(Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(1.0, 0.0)
        )));
        zones.add(createTestZone(Arrays.asList(
                Arrays.asList(1.0, 0.0),
                Arrays.asList(2.0, 0.0)
        )));

        Method method = GeometryConverter.class.getDeclaredMethod("mergeZoneCoordinates", List.class, double.class);
        method.setAccessible(true);
        Geometry result = (Geometry) method.invoke(geometryConverter, zones, 5.0);

        assertNotNull(result);
        assertTrue(result instanceof LineString || result instanceof MultiLineString);
    }

    @Test
    void testMergeLineStrings() throws Exception {
        List<List<List<Double>>> coordinatesList = new ArrayList<>();
        coordinatesList.add(Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(1.0, 0.0)
        ));
        coordinatesList.add(Arrays.asList(
                Arrays.asList(1.0, 0.0),
                Arrays.asList(2.0, 0.0)
        ));

        Method method = GeometryConverter.class.getDeclaredMethod("mergeLineStrings", List.class, double.class);
        method.setAccessible(true);
        Geometry result = (Geometry) method.invoke(geometryConverter, coordinatesList, 5.0);

        assertNotNull(result);
        if (result instanceof LineString) {
            assertEquals(3, ((LineString) result).getCoordinates().length);
        } else if (result instanceof MultiLineString) {
            assertEquals(2, result.getNumGeometries());
        }
    }

    @Test
    void testCreateLineString() throws Exception {
        List<List<Double>> coordinates = Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(1.0, 0.0)
        );

        Method method = GeometryConverter.class.getDeclaredMethod("createLineString", List.class);
        method.setAccessible(true);
        LineString result = (LineString) method.invoke(geometryConverter, coordinates);

        assertNotNull(result);
        assertEquals(2, result.getCoordinates().length);
        assertEquals(0.0, result.getCoordinates()[0].x);
        assertEquals(0.0, result.getCoordinates()[0].y);
    }

    @Test
    void testSnapAndMerge() throws Exception {
        LineString line1 = geometryFactory.createLineString(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(1, 0)
        });
        LineString line2 = geometryFactory.createLineString(new Coordinate[]{
                new Coordinate(1.1, 0),
                new Coordinate(2, 0)
        });
        GeometryCollection collection = geometryFactory.createGeometryCollection(new Geometry[]{line1, line2});

        Method method = GeometryConverter.class.getDeclaredMethod("snapAndMerge", Geometry.class, double.class);
        method.setAccessible(true);
        Geometry result = (Geometry) method.invoke(geometryConverter, collection, 0.2);

        assertNotNull(result);
        if (result instanceof LineString) {
            assertEquals(3, ((LineString) result).getCoordinates().length);
        } else {
            fail("Expected merged LineString");
        }
    }

    @Test
    void testSnapAndMergeWithNoSnapping() throws Exception {
        LineString line1 = geometryFactory.createLineString(new Coordinate[]{
                new Coordinate(0, 0),
                new Coordinate(1, 0)
        });
        LineString line2 = geometryFactory.createLineString(new Coordinate[]{
                new Coordinate(2, 0),
                new Coordinate(3, 0)
        });
        GeometryCollection collection = geometryFactory.createGeometryCollection(new Geometry[]{line1, line2});

        Method method = GeometryConverter.class.getDeclaredMethod("snapAndMerge", Geometry.class, double.class);
        method.setAccessible(true);
        Geometry result = (Geometry) method.invoke(geometryConverter, collection, 0.1);

        assertNotNull(result);
        assertTrue(result instanceof GeometryCollection);
        assertEquals(2, result.getNumGeometries());
    }

    private AreaWithSplitZonesDTO createTestAreaWithZones() {
        AreaWithSplitZonesDTO dto = new AreaWithSplitZonesDTO();

        // Create area geometry (square)
        Area area = new Area();
        GeometryData areaGeometry = new GeometryData();
        areaGeometry.setCoordinates(List.of(Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(1.0, 0.0),
                Arrays.asList(1.0, 1.0),
                Arrays.asList(0.0, 1.0),
                Arrays.asList(0.0, 0.0)
        )));
        area.setGeometry(areaGeometry);
        dto.setArea(area);

        // Create left zones (two connected lines)
        List<Zone> leftZones = new ArrayList<>();
        leftZones.add(createTestZone(Arrays.asList(
                Arrays.asList(0.0, 0.0),
                Arrays.asList(0.5, 0.0)
        )));
        leftZones.add(createTestZone(Arrays.asList(
                Arrays.asList(0.5, 0.0),
                Arrays.asList(1.0, 0.0)
        )));
        dto.setLeftZones(leftZones);

        // Create right zones (single line)
        List<Zone> rightZones = new ArrayList<>();
        rightZones.add(createTestZone(Arrays.asList(
                Arrays.asList(0.0, 1.0),
                Arrays.asList(1.0, 1.0)
        )));
        dto.setRightZones(rightZones);

        return dto;
    }

    private Zone createTestZone(List<List<Double>> coordinates) {
        Zone zone = new Zone();
        LineGeometryData geometry = new LineGeometryData();
        geometry.setCoordinates(coordinates);
        zone.setGeometry(geometry);
        return zone;
    }

    // Mock classes for testing if needed
    static class MockZoneGeometry {
        private List<List<Double>> coordinates;

        public List<List<Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<List<Double>> coordinates) {
            this.coordinates = coordinates;
        }
    }

    static class MockZone {
        private MockZoneGeometry geometry;

        public MockZoneGeometry getGeometry() {
            return geometry;
        }

        public void setGeometry(MockZoneGeometry geometry) {
            this.geometry = geometry;
        }
    }
}