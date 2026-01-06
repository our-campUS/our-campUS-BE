package com.campus.campus.domain.place.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.place.application.dto.response.LikeResponse;
import com.campus.campus.domain.place.application.dto.response.SavedPlaceInfo;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipMapResponse;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipScrollResponse;
import com.campus.campus.domain.place.application.service.PlaceService;
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

	@GetMapping("/search")
	@Operation(summary = "장소 키워드로 검색", description = "검색 결과 5개 검색되도록 함")
	public CommonResponse<List<SavedPlaceInfo>> getPlaceInfo(@RequestParam String keyword) {
		List<SavedPlaceInfo> searchResponse = placeService.search(keyword);
		return CommonResponse.success(PlaceResponseCode.PLACE_SEARCH_SUCCESS, searchResponse);
	}

	@PostMapping("/like-place")
	@Operation(summary = "장소 좋아요 누르기")
	public CommonResponse<LikeResponse> likePlace(@Valid @RequestBody SavedPlaceInfo request,
		@CurrentUserId Long userId) {
		LikeResponse response = placeService.likePlace(request, userId);
		return CommonResponse.success(PlaceResponseCode.PLACE_SAVE_SUCCESS, response);
	}

	@GetMapping("/partnership/list")
	@Operation(summary = "리스트로 제휴 전체 조회", description = "무한 스크롤 방식으로 제휴 장소 목록을 조회합니다.")
	public CommonResponse<PartnershipScrollResponse> getPartnershipPlaces(
		@CurrentUserId Long userId,
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
			example = "20"
		)
		@RequestParam(defaultValue = "20") int size) {
		PartnershipScrollResponse response = placeService.getPartnershipPlaces(userId, cursor, size);
		return CommonResponse.success(PlaceResponseCode.CHECK_PARTNERSHIP_PLACE_SUCCESS, response);
	}

	@GetMapping("/partnership/map")
	@Operation(
		summary = "지도에서 제휴 장소 조회",
		description = """
				현재 지도 화면(bounds) 안에 있는 제휴 장소들을 조회합니다.
				- bounds는 지도 화면의 남서/북동 좌표입니다.
				- 지도 이동 또는 확대/축소 시 재호출됩니다.
			""")
	public CommonResponse<List<PartnershipMapResponse>> getPartnershipMap(
		@CurrentUserId Long userId,
		@Parameter(
			description = "지도 화면의 남쪽(최소) 위도",
			example = "37.497"
		)
		@RequestParam double minLat,
		@Parameter(
			description = "지도 화면의 북쪽(최대) 위도",
			example = "37.512"
		)
		@RequestParam double maxLat,
		@Parameter(
			description = "지도 화면의 서쪽(최소) 경도",
			example = "126.953"
		)
		@RequestParam double minLng,
		@Parameter(
			description = "지도 화면의 동쪽(최대) 경도",
			example = "126.982"
		)
		@RequestParam double maxLng
	) {
		return CommonResponse.success(
			PlaceResponseCode.CHECK_PARTNERSHIP_PLACE_SUCCESS,
			placeService.getPartnershipPlacesForMap(userId, minLat, maxLat, minLng, maxLng)
		);
	}

}
