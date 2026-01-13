package com.campus.campus.domain.stamp.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.stamp.application.dto.response.RewardResponse;
import com.campus.campus.domain.stamp.application.service.StampService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stamps")
@RequiredArgsConstructor
public class StampController {
	private final StampService stampService;

	@GetMapping("/rewards")
	@Operation(summary = "스탬프 보상 목록 조회 기능")
	public CommonResponse<List<RewardResponse>> getRewards(@CurrentUserId Long userId) {
		List<RewardResponse> rewardResponse = stampService.findRewards(userId);

		return CommonResponse.success(StampResponseCode.REWARD_LIST_SUCCESS, rewardResponse);
	}
}
