package com.campus.campus.global.util.geocoder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceAreaChecker {

	private static final String GEOJSON_RESOURCE = "/seoul-gyeonggi.geojson";

	private static final double BBOX_MIN_LAT = 36.89;
	private static final double BBOX_MAX_LAT = 38.30;
	private static final double BBOX_MIN_LNG = 126.37;
	private static final double BBOX_MAX_LNG = 127.87;

	private final ObjectMapper objectMapper;
	private final GeometryFactory geometryFactory = new GeometryFactory();

	private PreparedGeometry serviceArea;

	@PostConstruct
	void init() {
		try (InputStream is = getClass().getResourceAsStream(GEOJSON_RESOURCE)) {
			if (is == null) {
				log.warn("서비스 영역 GeoJSON 파일을 찾을 수 없습니다 ({}). bbox 폴백 모드로 동작합니다.", GEOJSON_RESOURCE);
				return;
			}
			this.serviceArea = loadServiceArea(is);
			log.info("서비스 영역 GeoJSON 로드 완료 (vertices={})", serviceArea.getGeometry().getNumPoints());
		} catch (Exception e) {
			log.warn("서비스 영역 GeoJSON 로드 실패: {}. bbox 폴백 모드로 동작합니다.", e.getMessage());
			this.serviceArea = null;
		}
	}

	public boolean isInServiceArea(double lat, double lng) {
		if (serviceArea == null) {
			return isInBbox(lat, lng);
		}
		Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
		return serviceArea.contains(point);
	}

	public boolean isAddressInServiceArea(String address) {
		if (address == null) {
			return false;
		}
		String trimmed = address.trim();
		return trimmed.startsWith("서울") || trimmed.startsWith("경기");
	}

	private boolean isInBbox(double lat, double lng) {
		return lat >= BBOX_MIN_LAT && lat <= BBOX_MAX_LAT
			&& lng >= BBOX_MIN_LNG && lng <= BBOX_MAX_LNG;
	}

	private PreparedGeometry loadServiceArea(InputStream is) throws IOException {
		JsonNode root = objectMapper.readTree(is);
		JsonNode features = root.get("features");
		if (features == null || !features.isArray()) {
			throw new IOException("GeoJSON에 features 배열이 없습니다.");
		}

		List<Polygon> polygons = new ArrayList<>();
		for (JsonNode feature : features) {
			JsonNode geometry = feature.get("geometry");
			if (geometry == null) {
				continue;
			}
			collectPolygons(geometry, polygons);
		}

		if (polygons.isEmpty()) {
			throw new IOException("GeoJSON에서 Polygon을 찾지 못했습니다.");
		}

		MultiPolygon multi = geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[0]));
		return PreparedGeometryFactory.prepare(multi);
	}

	private void collectPolygons(JsonNode geometry, List<Polygon> out) {
		String type = geometry.get("type").asText();
		JsonNode coordinates = geometry.get("coordinates");
		if ("Polygon".equals(type)) {
			out.add(parsePolygon(coordinates));
		} else if ("MultiPolygon".equals(type)) {
			for (JsonNode polygonCoords : coordinates) {
				out.add(parsePolygon(polygonCoords));
			}
		}
	}

	private Polygon parsePolygon(JsonNode polygonCoords) {
		LinearRing outer = parseLinearRing(polygonCoords.get(0));
		LinearRing[] holes = new LinearRing[Math.max(0, polygonCoords.size() - 1)];
		for (int i = 1; i < polygonCoords.size(); i++) {
			holes[i - 1] = parseLinearRing(polygonCoords.get(i));
		}
		return geometryFactory.createPolygon(outer, holes);
	}

	private LinearRing parseLinearRing(JsonNode ringCoords) {
		Coordinate[] coords = new Coordinate[ringCoords.size()];
		for (int i = 0; i < ringCoords.size(); i++) {
			JsonNode point = ringCoords.get(i);
			double lng = point.get(0).asDouble();
			double lat = point.get(1).asDouble();
			coords[i] = new Coordinate(lng, lat);
		}
		return geometryFactory.createLinearRing(coords);
	}
}
