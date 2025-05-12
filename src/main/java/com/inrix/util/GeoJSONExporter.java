package com.inrix.util;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GeoJSONExporter {

    /**
     * Exports zone geometries to a GeoJSON file
     * @param zoneAreaGeometries List of zone geometries
     * @param outputPath Full path to output file
     * @throws IOException If file writing fails
     */
    public static void exportZonesToGeoJSON(List<List<Geometry>> zoneAreaGeometries, String outputPath) throws IOException {
        File outputFile = new File(outputPath);

        // Create parent directories if they don't exist
        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                throw new IOException("Failed to create directory: " + parent);
            }
        }

        GeoJsonWriter writer = new GeoJsonWriter();
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(generateGeoJSON(zoneAreaGeometries, writer));
        }
    }

    private static String generateGeoJSON(List<List<Geometry>> zones, GeoJsonWriter writer) {
        StringBuilder geoJson = new StringBuilder();
        geoJson.append("{\n  \"type\": \"FeatureCollection\",\n  \"features\": [\n");

        boolean firstZone = true;
        for (int zoneId = 0; zoneId < zones.size(); zoneId++) {
            List<Geometry> geometries = zones.get(zoneId);

            if (!geometries.isEmpty()) {
                if (!firstZone) {
                    geoJson.append(",\n");
                }
                geoJson.append(createFeature(writer, geometries.get(0), zoneId, "area"));
                firstZone = false;
            }

            if (geometries.size() > 1) {
                geoJson.append(",\n");
                geoJson.append(createFeature(writer, geometries.get(1), zoneId, "left_zone"));
            }

            if (geometries.size() > 2) {
                geoJson.append(",\n");
                geoJson.append(createFeature(writer, geometries.get(2), zoneId, "right_zone"));
            }
        }

        geoJson.append("\n  ]\n}");
        return geoJson.toString();
    }

    private static String createFeature(GeoJsonWriter writer, Geometry geometry, int zoneId, String type) {
        return String.format(
                "    {\n" +
                        "      \"type\": \"Feature\",\n" +
                        "      \"properties\": {\n" +
                        "        \"zone_id\": %d,\n" +
                        "        \"type\": \"%s\"\n" +
                        "      },\n" +
                        "      \"geometry\": %s\n" +
                        "    }",
                zoneId,
                type,
                // Indent the geometry JSON
                writer.write(geometry).replace("\n", "\n        ")
        );
    }
}