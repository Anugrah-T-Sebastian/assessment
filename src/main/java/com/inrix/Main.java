package com.inrix;

import com.inrix.loader.AreaZoneDataLoader;
import com.inrix.service.RoadSideProcessorService;
import com.inrix.util.GeometryConverter;
import com.inrix.util.ZoneProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final String AREA_FILE_PATH = "data/areas.json";
    private static final String ZONES_FILE_PATH = "data/zones.json";
    private static final String OUTPUT_FILE_PATH = "output/zones.geojson";

    public static void main(String[] args) {
        try {
            RoadSideProcessorService processorService = new RoadSideProcessorService();

            processorService.processAndExport(
                    AREA_FILE_PATH,
                    ZONES_FILE_PATH,
                    OUTPUT_FILE_PATH
            );
        } catch (Exception e) {
            logger.error("Error processing roads: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
