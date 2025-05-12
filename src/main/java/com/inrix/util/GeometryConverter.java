package com.inrix.util;

import com.inrix.dto.AreaWithSplitZonesDTO;
import com.inrix.model.Zone;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.operation.overlay.snap.GeometrySnapper;
import java.util.*;
import java.util.stream.Collectors;

public class GeometryConverter {
    private static final double DEFAULT_SNAP_TOLERANCE = 5.0;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public List<List<Geometry>> convertToGeometries(List<AreaWithSplitZonesDTO> processedAreas) {
        return processedAreas.stream()
                .map(this::convertAreaToGeometries)
                .collect(Collectors.toList());
    }

    private List<Geometry> convertAreaToGeometries(AreaWithSplitZonesDTO dto) {
        List<Geometry> geometries = new ArrayList<>();

        geometries.add(createPolygonFromCoordinates(dto.getArea().getGeometry().getCoordinates()));
        geometries.add(mergeZoneCoordinates(dto.getLeftZones(), DEFAULT_SNAP_TOLERANCE));
        geometries.add(mergeZoneCoordinates(dto.getRightZones(), DEFAULT_SNAP_TOLERANCE));

        return geometries;
    }

    private Polygon createPolygonFromCoordinates(List<List<Double>> coordinates) {
        Coordinate[] coords = coordinates.stream()
                .map(coord -> new Coordinate(coord.get(0), coord.get(1)))
                .toArray(Coordinate[]::new);
        return geometryFactory.createPolygon(coords);
    }

    private Geometry mergeZoneCoordinates(List<Zone> zones, double snapTolerance) {
        List<List<List<Double>>> coordinatesList = zones.stream()
                .map(zone -> zone.getGeometry().getCoordinates())
                .collect(Collectors.toList());
        return mergeLineStrings(coordinatesList, snapTolerance);
    }

    public Geometry mergeLineStrings(List<List<List<Double>>> coordinatesList, double snapTolerance) {
        List<LineString> lineStrings = coordinatesList.stream()
                .map(this::createLineString)
                .collect(Collectors.toList());

        LineMerger merger = new LineMerger();
        merger.add(lineStrings);
        Collection<LineString> merged = merger.getMergedLineStrings();

        Geometry result = merged.size() == 1
                ? merged.iterator().next()
                : geometryFactory.createMultiLineString(merged.toArray(new LineString[0]));

        return snapTolerance > 0 ? snapAndMerge(result, snapTolerance) : result;
    }

    private LineString createLineString(List<List<Double>> lineCoords) {
        Coordinate[] coordinates = lineCoords.stream()
                .map(point -> new Coordinate(point.get(0), point.get(1)))
                .toArray(Coordinate[]::new);
        return geometryFactory.createLineString(coordinates);
    }

    private Geometry snapAndMerge(Geometry geom, double tolerance) {
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

                    Geometry[] snappedGeoms = GeometrySnapper.snap(geom1, geom2, tolerance);
                    if (!snappedGeoms[0].equalsExact(geom1) || !snappedGeoms[1].equalsExact(geom2)) {
                        LineMerger merger = new LineMerger();
                        merger.add(snappedGeoms[0]);
                        merger.add(snappedGeoms[1]);
                        Collection<LineString> newMerged = merger.getMergedLineStrings();

                        if (newMerged.size() == 1) {
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

        return components.size() == 1
                ? components.get(0)
                : geometryFactory.createGeometryCollection(components.toArray(new Geometry[0]));
    }
}
