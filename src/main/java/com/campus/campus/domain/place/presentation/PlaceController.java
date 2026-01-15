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
import com.campus.campus.domain.place.application.dto.response.PartnershipPinResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.geocoder.AddressResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipResponse;
import com.campus.campus.domain.place.application.service.PartnershipPlaceService;
import com.campus.campus.domain.place.application.service.PlaceService;
import com.campus.campus.domain.place.infrastructure.geocoder.GeoCoderClient;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

	private final PlaceService placeService;
	private final PartnershipPlaceService partnershipPlaceService;
	private final GeoCoderClient geoCoderClient;

	@GetMapping("/search")
	@Operation(summary = "현위치 기반 가까운 순으로 장소 키워드 검색", description = "검색 결과 5개 검색되도록 함")
	public CommonResponse<List<SavedPlaceInfo>> getPlaceInfoWithLocationAndKeyword(
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
		List<SavedPlaceInfo> searchResponse = placeService.searchByLocationAndKeyword(lat, lng, keyword);

		return CommonResponse.success(PlaceResponseCode.PLACE_SEARCH_SUCCESS, searchResponse);
	}

	@GetMapping("/search/keyword")
	@Operation(summary = "키워드 기반 장소 검색")
	public CommonResponse<List<SavedPlaceInfo>> getPlaceInfoWithKeyword(@RequestParam String keyword) {
		List<SavedPlaceInfo> searchResponse = placeService.searchByKeyword(keyword);

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

	@GetMapping("/partnership")
	@Operation(summary = "리스트로 제휴 장소 전체 조회", description = "무한 스크롤 방식으로 제휴 장소 목록을 조회합니다.")
	public CommonResponse<List<PartnershipResponse>> getPartnershipPlaces(
		@CurrentUserId Long userId,
		@Parameter(
			description = "현재 위치의 위도",
			example = "37.50415"
		)
		@RequestParam double lat,
		@Parameter(
			description = "현재 위치의 경도",
			example = "126.9570"
		)
		@RequestParam double lng,
		@Parameter(
			description = """
				무한 스크롤 커서 값.
				- 첫 요청 시 null
				- 다음 요청부터는 이전 응답의 nextCursor 값
				""",
			examples = {
				@ExampleObject(
					name = "첫 요청",
					value = "null"
				),
				@ExampleObject(
					name = "다음 요청",
					value = "120"
				)
			}
		)
		@RequestParam(required = false) Long cursor,
		@Parameter(
			description = "한 번에 조회할 개수",
			example = "5"
		)
		@RequestParam(defaultValue = "5") int size) {
		List<PartnershipResponse> response = partnershipPlaceService.getPartnershipPlaces(userId, cursor, size, lat,
			lng);

		return CommonResponse.success(PlaceResponseCode.CHECK_PARTNERSHIP_PLACES_SUCCESS, response);
	}

	@GetMapping("/partnership/map")
	@Operation(
		summary = "지도에서 제휴 장소 조회",
		description = """
				현재 지도 화면(bounds) 안에 있는 제휴 장소들을 조회합니다.
				- bounds는 지도 화면의 남서/북동 좌표입니다.
				- 지도 이동 또는 확대/축소 시 재호출됩니다.
			""")
	public CommonResponse<List<PartnershipPinResponse>> getPartnershipPlacesInMap(
		@CurrentUserId Long userId,
		@Parameter(
			description = "지도 화면의 남쪽(최소) 위도",
			example = "37.497"
		)
		@RequestParam Double minLat,
		@Parameter(
			description = "지도 화면의 북쪽(최대) 위도",
			example = "37.512"
		)
		@RequestParam Double maxLat,
		@Parameter(
			description = "지도 화면의 서쪽(최소) 경도",
			example = "126.953"
		)
		@RequestParam Double minLng,
		@Parameter(
			description = "지도 화면의 동쪽(최대) 경도",
			example = "126.982"
		)
		@RequestParam Double maxLng
	) {
		return CommonResponse.success(
			PlaceResponseCode.CHECK_PARTNERSHIP_PLACES_SUCCESS,
			partnershipPlaceService.findPartnerInBounds(userId, minLat, maxLat, minLng, maxLng)
		);
	}

	@GetMapping("/partnership/detail")
	@Operation(summary = "제휴 장소 상세 조회(맵에서 핀 클릭 시)")
	public CommonResponse<PartnershipResponse> getPartnershipPlaceDetail(
		@Parameter(description = "현재 위치의 위도", example = "37.50415")
		@RequestParam double lat,

		@Parameter(description = "현재 위치의 경도", example = "126.9570")
		@RequestParam double lng,

		@Parameter(description = "제휴글 ID", example = "10")
		@RequestParam Long postId,

		@CurrentUserId Long userId
	) {
		return CommonResponse.success(
			PlaceResponseCode.CHECK_ONE_PARTNERSHIP_PLACE_SUCCESS,
			partnershipPlaceService.getPartnershipDetail(postId, userId, lat, lng));
	}

	@PostMapping("/suggest-partnership")
	@Operation(summary = "제휴 신청하기")
	public CommonResponse<Void> suggestPartnership(
		@CurrentUserId Long userId,
		@Valid @RequestBody SavedPlaceInfo placeInfo
	) {
		placeService.suggestPartnership(userId, placeInfo);
		return CommonResponse.success(PlaceResponseCode.PARTNERSHIP_SUGGEST_SUCCESS);
	}

}
