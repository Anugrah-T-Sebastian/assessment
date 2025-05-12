package com.inrix.service;

import com.inrix.dto.AreaWithSplitZonesDTO;
import com.inrix.loader.AreaZoneDataLoader;
import com.inrix.model.Area;
import com.inrix.model.Zone;
import com.inrix.util.GeoJSONExporter;
import com.inrix.util.GeometryConverter;
import com.inrix.util.ZoneProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.*;

import static org.mockito.Mockito.*;

class RoadSideProcessorServiceTest {

    private RoadSideProcessorService processorService;
    private AreaZoneDataLoader mockDataLoader;
    private ZoneProcessor mockZoneProcessor;
    private GeometryConverter mockGeometryConverter;

    @BeforeEach
    void setUp() {
        // Create mocks
        mockDataLoader = mock(AreaZoneDataLoader.class);
        mockZoneProcessor = mock(ZoneProcessor.class);
        mockGeometryConverter = mock(GeometryConverter.class);

        // Create service with mocked dependencies
        processorService = new RoadSideProcessorService(mockDataLoader, mockZoneProcessor, mockGeometryConverter);
    }

    @Test
    void testProcessAndExport() throws Exception {
        // Arrange
        String areasFile = "data/areas.json";
        String zonesFile = "data/zones.json";
        String outputPath = "output/zones.geojson";

        Map<String, Area> mockAreas = new HashMap<>();
        List<Zone> mockZones = new ArrayList<>();
        when(mockDataLoader.loadAreas(areasFile)).thenReturn(mockAreas);
        when(mockDataLoader.loadZones(zonesFile)).thenReturn(mockZones);

        List<AreaWithSplitZonesDTO> mockProcessedAreas = new ArrayList<>();
        when(mockZoneProcessor.processAreasWithSplitZones(mockAreas, mockZones)).thenReturn(mockProcessedAreas);

        List<List<Geometry>> mockGeometries = new ArrayList<>();
        when(mockGeometryConverter.convertToGeometries(mockProcessedAreas)).thenReturn(mockGeometries);

        // Mock the static GeoJSONExporter
        try (MockedStatic<GeoJSONExporter> mockedExporter = Mockito.mockStatic(GeoJSONExporter.class)) {
            // Act
            processorService.processAndExport(areasFile, zonesFile, outputPath);

            // Assert
            verify(mockDataLoader).loadAreas(areasFile);
            verify(mockDataLoader).loadZones(zonesFile);
            verify(mockZoneProcessor).processAreasWithSplitZones(mockAreas, mockZones);
            verify(mockGeometryConverter).convertToGeometries(mockProcessedAreas);
            mockedExporter.verify(() -> GeoJSONExporter.exportZonesToGeoJSON(mockGeometries, outputPath));
        }
    }
}