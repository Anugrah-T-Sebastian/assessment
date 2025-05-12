package com.inrix;

import com.inrix.dto.AreaWithSplitZonesDTO;
import com.inrix.model.*;
import com.inrix.util.GeoJSONExporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.overlay.snap.GeometrySnapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RoadSideGenerator {
    private static final Logger logger = LoggerFactory.getLogger(RoadSideGenerator.class);
    private static final double DEFAULT_ROAD_WIDTH = 3.0;

    public static void main(String[] args) {
        try {
            CommandLineArgs cliArgs = CommandLineArgs.parse(args);
            if (cliArgs == null) System.exit(1);

            RoadSideGenerator generator = new RoadSideGenerator();
            generator.getGeoMap(cliArgs.getAreasFile(),
                    cliArgs.getZonesFile(),
                    cliArgs.getRoadWidth()
            );
//            List<Geometry> roadSides = generator.processRoads(
//                    cliArgs.getAreasFile(),
//                    cliArgs.getZonesFile(),
//                    cliArgs.getRoadWidth()
//            );

//            GeoJsonUtils.writeGeoJson(roadSides, cliArgs.getOutputFile());
//            logger.info("Generated {} road sides to {}", roadSides.size(), cliArgs.getOutputFile());
        } catch (Exception e) {
            logger.error("Error processing roads: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    public void getGeoMap(String areasFile, String zonesFile, double defaultWidth) throws Exception {
        // Load data from files
        Map<String, Area> stringAreaMap = loadAreas(areasFile);
        List<Zone> zones = loadZones(zonesFile);

        // Local list for the new DTOs
        List<AreaWithSplitZonesDTO> areaWithSplitZones = new ArrayList<>();

        // Create zone lookup map
        Map<String, Zone> zoneMap = zones.stream()
                .collect(Collectors.toMap(Zone::getCurbZoneId, Function.identity()));

        // Process each area
        for (Area area : stringAreaMap.values()) {
            List<Zone> leftZones = new ArrayList<>();
            List<Zone> rightZones = new ArrayList<>();

            for (String curbZoneId : area.getCurbZoneIds()) {
                Zone zone = zoneMap.get(curbZoneId);
                if (zone != null) {
                    // Determine zone side from location references
                    String side = determineZoneSide(zone);
                    if ("left".equalsIgnoreCase(side)) {
                        leftZones.add(zone);
                    } else if ("right".equalsIgnoreCase(side)) {
                        rightZones.add(zone);
                    }
                    // Zones without clear side designation are ignored
                }
            }

            // Create and add the new DTO
            areaWithSplitZones.add(new AreaWithSplitZonesDTO(area, leftZones, rightZones));


        }

        // Output results
        System.out.println("Processed " + areaWithSplitZones.size() + " areas with split zones");
        areaWithSplitZones.forEach(dto -> {
            System.out.println("Area " + dto.getArea().toString() +
                    " has " + dto.getLeftZones().size() + " left zones and " +
                    dto.getRightZones().size() + " right zones");
        });

        List<List<Geometry>> zoneAreaGeometries = new ArrayList<>();
        for (int i = 0; i < areaWithSplitZones.size(); i++) {
            List<Geometry> zoneAreaGeometry = new ArrayList<>();
            GeometryFactory geometryFactory = new GeometryFactory();
            List<List<Double>> coordinates = areaWithSplitZones.get(i).getArea().getGeometry().getCoordinates();
            Coordinate[] coords = new Coordinate[coordinates.size()];
            for (int j = 0; j < coordinates.size(); j++) {
                List<Double> coord = coordinates.get(j);
//            if (coord == null || coord.size() < 2) {
//                throw new IllegalArgumentException("Each coordinate must have at least 2 values");
//            }
                coords[j] = new Coordinate(coord.get(0), coord.get(1));
            }
            Polygon polygon = geometryFactory.createPolygon(coords);
            System.out.println(polygon.toString());
            zoneAreaGeometry.add(polygon);

            List<List<List<Double>>> leftZoneCoordinates = areaWithSplitZones.get(i).getLeftZones().stream().map(zone -> zone.getGeometry().getCoordinates()).collect(Collectors.toList());
            Geometry mergedLeft = mergeLineStrings(leftZoneCoordinates, 5.0);
            System.out.println(mergedLeft.toString());
            zoneAreaGeometry.add(mergedLeft);

            List<List<List<Double>>> rightZoneCoordinates = areaWithSplitZones.get(i).getRightZones().stream().map(zone -> zone.getGeometry().getCoordinates()).collect(Collectors.toList());
            Geometry mergedRight = mergeLineStrings(rightZoneCoordinates, 5.0);
            System.out.println(mergedRight.toString());
            zoneAreaGeometry.add(mergedRight);

            zoneAreaGeometries.add(zoneAreaGeometry);
        }

        try {
            GeoJSONExporter.exportZonesToGeoJSON(zoneAreaGeometries, "output/zones.geojson");
        } catch (IOException e) {
            // Handle error
            System.out.println("Error in writing geojson" + e);
        }
    }

    public static Geometry mergeLineStrings(List<List<List<Double>>> coordinatesList, double snapTolerance) {
        GeometryFactory gf = new GeometryFactory();

        // 1. Convert List<List<List<Double>>> to List<LineString>
        List<LineString> lineStrings = new ArrayList<>();
        for (List<List<Double>> lineCoords : coordinatesList) {
            Coordinate[] coordinates = new Coordinate[lineCoords.size()];
            for (int i = 0; i < lineCoords.size(); i++) {
                List<Double> point = lineCoords.get(i);
                coordinates[i] = new Coordinate(point.get(0), point.get(1));
            }
            lineStrings.add(gf.createLineString(coordinates));
        }

        // 2. First attempt: Merge naturally connected lines
        LineMerger merger = new LineMerger();
        merger.add(lineStrings);
        Collection<LineString> merged = merger.getMergedLineStrings();

        // 3. Create initial merged geometry
        Geometry result;
        if (merged.size() == 1) {
            result = merged.iterator().next();
        } else {
            result = gf.createMultiLineString(merged.toArray(new LineString[0]));
        }

        // 4. If disconnected and snapTolerance > 0, attempt snapping
        if (snapTolerance > 0) {
            result = snapAndMerge(result, snapTolerance, gf);
        }

        return result;
    }

    private static Geometry snapAndMerge(Geometry geom, double tolerance, GeometryFactory gf) {
        // Convert to list of components
        List<Geometry> components = new ArrayList<>();
        for (int i = 0; i < geom.getNumGeometries(); i++) {
            components.add(geom.getGeometryN(i));
        }

        boolean improved;
        do {
            improved = false;
            for (int i = 0; i < components.size(); i++) {
                for (int j = i + 1; j < components.size(); j++) {
                    Geometry geom1 = components.get(i);
                    Geometry geom2 = components.get(j);

                    // Corrected snapping approach
                    Geometry[] snappedGeoms = GeometrySnapper.snap(geom1, geom2, tolerance);
                    Geometry snapped1 = snappedGeoms[0];
                    Geometry snapped2 = snappedGeoms[1];

                    // If snapping occurred, try to merge
                    if (!snapped1.equalsExact(geom1) || !snapped2.equalsExact(geom2)) {
                        LineMerger merger = new LineMerger();
                        merger.add(snapped1);
                        merger.add(snapped2);
                        Collection<LineString> newMerged = merger.getMergedLineStrings();

                        if (newMerged.size() == 1) {
                            // Successfully merged - replace the two components with merged result
                            components.remove(j);
                            components.remove(i);
                            components.add(newMerged.iterator().next());
                            improved = true;
                            break;
                        }
                    }
                }
                if (improved) break;
            }
        } while (improved && components.size() > 1);

        // Return the best result we found
        if (components.size() == 1) {
            return components.get(0);
        } else {
            return gf.createGeometryCollection(components.toArray(new Geometry[0]));
        }
    }
    private String determineZoneSide(Zone zone) {
        if (zone.getLocationReferences() != null) {
            for (Zone.LocationReference ref : zone.getLocationReferences()) {
                if (ref.getSide() != null && !ref.getSide().isEmpty()) {
                    return ref.getSide().toLowerCase();
                }
            }
        }
        return null; // or "unknown" if you prefer
    }

    private Map<String, Area> loadAreas(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), Area.AreaCollection.class)
                .getAreas().stream()
                .collect(Collectors.toMap(
                        area -> area.getCurbAreaId().toString(),
                        area -> area
                ));
    }

    private List<Zone> loadZones(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filePath), ZonesCollection.class).getZones();
    }

    private Area findMatchingArea(Zone zone, Map<String, Area> areas) {
        return areas.values().stream()
                .filter(area -> area.getCurbZoneIds().contains(zone.getCurbZoneId()))
                .findFirst()
                .orElse(null);
    }

    static class CommandLineArgs {
        private final String areasFile;
        private final String zonesFile;
        private final String outputFile;
        private final double roadWidth;

        private CommandLineArgs(String areasFile, String zonesFile, String outputFile, double roadWidth) {
            this.areasFile = areasFile;
            this.zonesFile = zonesFile;
            this.outputFile = outputFile;
            this.roadWidth = roadWidth;
        }

        public static CommandLineArgs parse(String[] args) {
            if (args.length < 2) {
                System.err.println("Usage: java -jar road-side-generator.jar <areas.json> <zones.json> [output.geojson] [--width <meters>]");
                return null;
            }
            String outputFile = args.length > 2 ? args[2] : "output.geojson";
            double width = DEFAULT_ROAD_WIDTH;
            for (int i = 3; i < args.length; i++) {
                if ("--width".equals(args[i]) && i + 1 < args.length) {
                    try {
                        width = Double.parseDouble(args[i + 1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid width value: " + args[i + 1]);
                        return null;
                    }
                }
            }
            return new CommandLineArgs(args[0], args[1], outputFile, width);
        }

        public String getAreasFile() { return areasFile; }
        public String getZonesFile() { return zonesFile; }
        public double getRoadWidth() { return roadWidth; }
    }
}