package com.campus.campus.domain.place.application.service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPlaceCacheService {
	private final RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper;

	private static final Duration CACHE_TTL = Duration.ofHours(1);
	private static final double GRID_PRECISION = 1000.0;

	public void cachePlaces(String keyword, double lat, double lng, List<SavedPlaceInfo> places) {
		if (places == null || places.isEmpty()) {
			return;
		}

		String key = generateKey(lat, lng, keyword);
		try {
			String jsonValue = objectMapper.writeValueAsString(places);
			redisTemplate.opsForValue().set(key, jsonValue, CACHE_TTL);
			log.info("[Redis] Cache Saved: key={}", key);
		} catch (Exception e) {
			log.warn("[Redis] Cache Save Failed: key={}", key, e);
		}
	}

	public Optional<List<SavedPlaceInfo>> getCachedPlaces(double lat, double lng, String keyword) {
		String key = generateKey(lat, lng, keyword);

		Object value = redisTemplate.opsForValue().get(key);
		if (value == null) {
			return Optional.empty();
		}

		try {
			String jsonValue = String.valueOf(value);
			List<SavedPlaceInfo> places = objectMapper.readValue(jsonValue, new TypeReference<List<SavedPlaceInfo>>() {
			});
			log.info("[Redis] Cache Hit: key={}", key);
			return Optional.of(places);
		} catch (Exception e) {
			log.warn("[Redis] Cache Parsing Failed: key={}", key, e);
			return Optional.empty();
		}
	}

	private String generateKey(double lat, double lng, String keyword) {
		double roundedLat = Math.round(lat * GRID_PRECISION) / GRID_PRECISION;
		double roundedLng = Math.round(lng * GRID_PRECISION) / GRID_PRECISION;

		return String.format("places:recommend:%s:%s:%s", keyword, roundedLat, roundedLng);
	}
}
