package com.campus.campus.domain.partnership.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.partnership.application.dto.response.PartnershipPinResponse;
import com.campus.campus.domain.partnership.application.service.PartnershipService;
import com.campus.campus.domain.place.application.dto.response.partnership.PartnershipResponse;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/partnership")
@RequiredArgsConstructor
public class PartnershipController {

	private final PartnershipService partnershipService;

	@GetMapping("/list")
	@Operation(summary = "리스트로 제휴 전체 조회", description = "무한 스크롤 방식으로 제휴 장소 목록을 조회합니다.")
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
		List<PartnershipResponse> response = partnershipService.getPartnershipPlaces(userId, cursor, size, lat, lng);
		return CommonResponse.success(PartnershipResponseCode.CHECK_PARTNERSHIP_PLACES_SUCCESS, response);
	}

	@GetMapping("/map")
	@Operation(
		summary = "지도에서 제휴 장소 조회",
		description = """
				현재 지도 화면(bounds) 안에 있는 제휴 장소들을 조회합니다.
				- bounds는 지도 화면의 남서/북동 좌표입니다.
				- 지도 이동 또는 확대/축소 시 재호출됩니다.
			""")
	public CommonResponse<List<PartnershipPinResponse>> getPartnershipMap(
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
			PartnershipResponseCode.CHECK_PARTNERSHIP_PLACES_SUCCESS,
			partnershipService.findPartnerInBounds(userId, minLat, maxLat, minLng, maxLng)
		);
	}

	@GetMapping("/detail")
	@Operation(summary = "제휴 장소 상세 조회(맵에서 핀 클릭 시)")
	public CommonResponse<PartnershipResponse> getPartnershipDetail(
		@Parameter(description = "현재 위치의 위도", example = "37.50415")
		@RequestParam double lat,

		@Parameter(description = "현재 위치의 경도", example = "126.9570")
		@RequestParam double lng,

		@Parameter(description = "제휴글 ID", example = "10")
		@RequestParam Long postId,

		@CurrentUserId Long userId
	) {
		return CommonResponse.success(
			PartnershipResponseCode.CHECK_ONE_PARTNERSHIP_PLACE_SUCCESS,
			partnershipService.getPartnershipDetail(postId, userId, lat, lng));
	}
}
