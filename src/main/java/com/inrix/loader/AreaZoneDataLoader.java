package com.inrix.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inrix.model.Area;
import com.inrix.model.Zone;
import com.inrix.model.ZonesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AreaZoneDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(AreaZoneDataLoader.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Area> loadAreas(String filePath) throws Exception {
        try {
            return objectMapper.readValue(new File(filePath), Area.AreaCollection.class)
                    .getAreas().stream()
                    .collect(Collectors.toMap(
                            area -> area.getCurbAreaId().toString(),
                            area -> area
                    ));
        } catch (Exception e) {
            logger.error("Failed to load areas from file: {}", filePath, e);
            throw new Exception("Error loading areas", e);
        }
    }

    public List<Zone> loadZones(String filePath) throws Exception {
        try {
            return objectMapper.readValue(new File(filePath), ZonesCollection.class).getZones();
        } catch (Exception e) {
            logger.error("Failed to load zones from file: {}", filePath, e);
            throw new Exception("Error loading zones", e);
        }
    }
}