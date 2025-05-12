package com.inrix;

import com.inrix.loader.AreaZoneDataLoader;
import com.inrix.service.RoadSideProcessorService;
import com.inrix.util.GeometryConverter;
import com.inrix.util.ZoneProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final  String areaFilePath = "data/areas.json";
    private static final  String zonesFilePath = "data/zones.json";
    private static final  String outputFilePath = "output/zones.geojson";

    public static void main(String[] args) {
        try {

            RoadSideProcessorService processorService = new RoadSideProcessorService(
                    new AreaZoneDataLoader(),
                    new ZoneProcessor(),
                    new GeometryConverter()
            );

            processorService.processAndExport(
                    areaFilePath,
                    zonesFilePath,
                    outputFilePath
            );
        } catch (Exception e) {
            logger.error("Error processing roads: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
