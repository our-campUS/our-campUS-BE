package com.campus.campus.domain.place.application.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.naver.NaverSearchResponse;
import com.campus.campus.domain.place.application.mapper.PlaceMapper;
import com.campus.campus.domain.place.domain.entity.Place;
import com.campus.campus.domain.place.domain.repository.PlaceRepository;
import com.campus.campus.domain.place.infrastructure.naver.NaverMapClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MapService {

	private final NaverMapClient naverMapClient;
	private final PlaceImagesService placeImagesService;
	private final PlaceMapper mapper;
	private final PlaceRepository placeRepository;

	public List<SavedPlaceInfo> search(String keyword) {
		//네이버에서 특정 장소 기본정보 받아오기
		NaverSearchResponse response = naverMapClient.searchPlaces(keyword, 5);

		return response.items().stream()
			.map(item -> {

				String name = mapper.stripHtml(item.title());
				String address = item.roadAddress();

				//placeKey 생성
				String placeKey = PlaceKeyGenerator.generate(name, address);

				//place upsert
				upsertPlace(mapper.toEntity(item, placeKey));

				//네이버 -> 기본 정보
				SavedPlaceInfo base = mapper.toSavedPlaceInfo(item, placeKey);

				//구글 -> 이미지 가져오기
				List<String> images = placeImagesService.getPlaceImgs(
					placeKey,
					name,
					address
				);

				return mapper.toSavedPlaceInfo(item, placeKey)
					.withImages(images);
			})
			.toList();
	}

	private void upsertPlace(Place place) {
		try {
			placeRepository.save(place);
		} catch (DataIntegrityViolationException e) {

		}
	}

}
