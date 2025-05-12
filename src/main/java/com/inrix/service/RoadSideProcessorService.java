package com.inrix.service;

import com.inrix.dto.AreaWithSplitZonesDTO;
import com.inrix.loader.AreaZoneDataLoader;
import com.inrix.model.Area;
import com.inrix.model.Zone;
import com.inrix.util.GeoJSONExporter;
import com.inrix.util.GeometryConverter;
import com.inrix.util.ZoneProcessor;
import org.locationtech.jts.geom.Geometry;

import java.util.List;
import java.util.Map;

public class RoadSideProcessorService {
    private final AreaZoneDataLoader dataLoader;
    private final ZoneProcessor zoneProcessor;
    private final GeometryConverter geometryConverter;

    public RoadSideProcessorService(
            AreaZoneDataLoader dataLoader,
            ZoneProcessor zoneProcessor,
            GeometryConverter geometryConverter) {
        this.dataLoader = dataLoader;
        this.zoneProcessor = zoneProcessor;
        this.geometryConverter = geometryConverter;
    }

    public void processAndExport(String areasFile, String zonesFile, String outputPath) throws Exception {
        // Load data
        Map<String, Area> areas = dataLoader.loadAreas(areasFile);
        List<Zone> zones = dataLoader.loadZones(zonesFile);

        // Process zones
        List<AreaWithSplitZonesDTO> processedAreas = zoneProcessor.processAreasWithSplitZones(areas, zones);
        logProcessedAreas(processedAreas);

        // Convert to geometries
        List<List<Geometry>> zoneAreaGeometries = geometryConverter.convertToGeometries(processedAreas);

        // Export
        GeoJSONExporter.exportZonesToGeoJSON(zoneAreaGeometries, outputPath);
    }

    private void logProcessedAreas(List<AreaWithSplitZonesDTO> processedAreas) {
        System.out.println("Processed " + processedAreas.size() + " areas with split zones");
        processedAreas.forEach(dto -> {
            System.out.println("Area " + dto.getArea().toString() +
                    " has " + dto.getLeftZones().size() + " left zones and " +
                    dto.getRightZones().size() + " right zones");
        });
    }
}