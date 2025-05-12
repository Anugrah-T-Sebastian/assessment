package com.inrix.util;

import com.inrix.dto.AreaWithSplitZonesDTO;
import com.inrix.model.Area;
import com.inrix.model.Zone;
import java.util.*;
import java.util.stream.Collectors;

public class ZoneProcessor {
    public List<AreaWithSplitZonesDTO> processAreasWithSplitZones(Map<String, Area> areas, List<Zone> zones) {
        Map<String, Zone> zoneMap = createZoneMap(zones);
        return areas.values().stream()
                .map(area -> createAreaWithSplitZones(area, zoneMap))
                .collect(Collectors.toList());
    }

    private AreaWithSplitZonesDTO createAreaWithSplitZones(Area area, Map<String, Zone> zoneMap) {
        Map<String, List<Zone>> splitZones = splitZonesBySide(area, zoneMap);
        return new AreaWithSplitZonesDTO(area, splitZones.get("left"), splitZones.get("right"));
    }

    private Map<String, Zone> createZoneMap(List<Zone> zones) {
        return zones.stream()
                .collect(Collectors.toMap(Zone::getCurbZoneId, zone -> zone));
    }

    private Map<String, List<Zone>> splitZonesBySide(Area area, Map<String, Zone> zoneMap) {
        List<Zone> leftZones = new ArrayList<>();
        List<Zone> rightZones = new ArrayList<>();

        area.getCurbZoneIds().forEach(curbZoneId -> {
            Zone zone = zoneMap.get(curbZoneId);
            if (zone != null) {
                String side = determineZoneSide(zone);
                if ("left".equalsIgnoreCase(side)) {
                    leftZones.add(zone);
                } else if ("right".equalsIgnoreCase(side)) {
                    rightZones.add(zone);
                }
            }
        });

        return Map.of("left", leftZones, "right", rightZones);
    }

    private String determineZoneSide(Zone zone) {
        return zone.getLocationReferences().stream()
                .filter(ref -> ref.getSide() != null && !ref.getSide().isEmpty())
                .map(ref -> ref.getSide().toLowerCase())
                .findFirst()
                .orElse("unknown");
    }
}