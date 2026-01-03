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
import com.campus.campus.domain.place.application.service.LikePlaceService;
import com.campus.campus.domain.place.application.service.MapService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/places")
@RequiredArgsConstructor
public class PlaceController {

	private final LikePlaceService likePlaceService;
	private final MapService mapService;

	@GetMapping("/search")
	@Operation(summary = "장소 키워드로 검색", description = "검색 결과 5개 검색되도록 함")
	public CommonResponse<List<SavedPlaceInfo>> getPlaceInfo(@RequestParam String keyword) {
		List<SavedPlaceInfo> searchResponse = mapService.search(keyword);
		return CommonResponse.success(PlaceResponseCode.PLACE_SEARCH_SUCCESS, searchResponse);
	}

	@PostMapping("/{placeKey}/like-place")
	@Operation(summary = "장소 좋아요 누르기")
	public CommonResponse<LikeResponse> likePlace(@RequestBody SavedPlaceInfo request, @CurrentUserId Long userId) {
		LikeResponse response = likePlaceService.likePlace(request, userId);
		return CommonResponse.success(PlaceResponseCode.PLACE_SAVE_SUCCESS, response);
	}
}
