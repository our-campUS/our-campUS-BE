package com.campus.campus.domain.place.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.geocoder.AddressResponse;
import com.campus.campus.domain.place.application.service.PlaceService;
import com.campus.campus.domain.place.infrastructure.geocoder.GeoCoderClient;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

	private final PlaceService placeService;
	private final GeoCoderClient geoCoderClient;

	@GetMapping("/search")
	@Operation(summary = "현위치 기반 가까운 순으로 장소 키워드 검색", description = "검색 결과 5개 검색되도록 함")
	public CommonResponse<List<SavedPlaceInfo>> getPlaceInfo(
		@Parameter(
			description = "검색할 키워드",
			example = "스타벅스"
		)
		@RequestParam String keyword,
		@Parameter(
			description = "현재 위치의 위도",
			example = "37.50415"
		)
		@RequestParam double lat,
		@Parameter(
			description = "현재 위치의 경도",
			example = "126.9570"
		)
		@RequestParam double lng
	) {
		List<SavedPlaceInfo> searchResponse = placeService.search(lat, lng, keyword);
		return CommonResponse.success(PlaceResponseCode.PLACE_SEARCH_SUCCESS, searchResponse);
	}

	@GetMapping
	public ResponseEntity<AddressResponse> getAddress(
		@Parameter(
			description = "현재 위치의 위도",
			example = "37.50415"
		)
		@RequestParam double lat,
		@Parameter(
			description = "현재 위치의 경도",
			example = "126.9570"
		)
		@RequestParam double lng
	) {
		return ResponseEntity.ok(geoCoderClient.getAddress(lat, lng));
	}

	@PostMapping("/like-place")
	@Operation(summary = "장소 좋아요 누르기")
	public CommonResponse<LikeResponse> likePlace(@Valid @RequestBody SavedPlaceInfo request,
		@CurrentUserId Long userId) {
		LikeResponse response = placeService.likePlace(request, userId);
		return CommonResponse.success(PlaceResponseCode.PLACE_SAVE_SUCCESS, response);
	}

	//가게 상세 조회 (리뷰 기능 구현 완료 후)

}
