package com.inrix.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inrix.model.Area;
import com.inrix.model.Zone;
import com.inrix.model.ZonesCollection;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AreaZoneDataLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Area> loadAreas(String filePath) throws Exception {
        return objectMapper.readValue(new File(filePath), Area.AreaCollection.class)
                .getAreas().stream()
                .collect(Collectors.toMap(
                        area -> area.getCurbAreaId().toString(),
                        area -> area
                ));
    }

    public List<Zone> loadZones(String filePath) throws Exception {
        return objectMapper.readValue(new File(filePath), ZonesCollection.class).getZones();
    }
}