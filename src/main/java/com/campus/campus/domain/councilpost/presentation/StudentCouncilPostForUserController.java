package com.campus.campus.domain.councilpost.presentation;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.council.domain.entity.CouncilType;
import com.campus.campus.domain.councilpost.application.dto.response.GetActivePartnershipListForUserResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetLikedPostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.LikePostResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostForUserResponse;
import com.campus.campus.domain.councilpost.application.dto.response.TodayEventResponse;
import com.campus.campus.domain.councilpost.application.service.StudentCouncilPostForUserService;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/users/student-council/posts")
@Tag(name = "Student Council Post For User", description = "사용자(USER) 권한 전용 사용자가 속한 대학/단과대/전공의 제휴/행사 게시글 목록 조회 API")
@RequiredArgsConstructor
@Slf4j
public class StudentCouncilPostForUserController {
	private final StudentCouncilPostForUserService postService;

	@PostMapping("/{postId}/like")
	@Operation(summary = "학생회 게시글 좋아요 토글")
	public CommonResponse<LikePostResponse> togglePostLike(@PathVariable Long postId, @CurrentUserId Long userId) {
		LikePostResponse response = postService.toggleLikePost(userId, postId);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIKE_SUCCESS, response);
	}

	@GetMapping("/likes")
	@Operation(summary = "관심 학생회 게시글 목록 조회")
	public CommonResponse<Page<GetLikedPostResponse>> getLikedPosts(
		@RequestParam(required = false) PostCategory category,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "3") int size,
		@CurrentUserId Long userId
	) {
		Page<GetLikedPostResponse> responses = postService.findLikedPosts(category, page, size, userId);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS, responses);
	}

	@GetMapping
	@Operation(
		summary = "학생회 타입별 게시글 목록 조회",
		description =
			"현재 로그인한 사용자가 속한 학생회 범위의 게시글 목록을 **타입별로** 조회합니다.\n\n" +
				"### ✅ councilType (필수)\n" +
				"- `SCHOOL_COUNCIL` : 학교 학생회\n" +
				"- `COLLEGE_COUNCIL` : 단과대 학생회 (단과대 정보 미설정 시 예외)\n" +
				"- `MAJOR_COUNCIL` : 전공 학생회 (전공 정보 미설정 시 예외)\n\n" +
				"### ✅ 필터/페이징\n" +
				"- `category`를 전달하면 해당 카테고리만 조회합니다. (미전달 시 전체)\n" +
				"- `page`는 1부터 시작합니다.\n" +
				"- `size`는 한 페이지당 조회 개수입니다.\n\n" +
				"### ✅ 현재 게시글 제외 조회(상세 하단 '다른 글' 용도)\n" +
				"- `excludePostId`를 전달하면 해당 게시글 ID를 목록에서 제외하고 조회합니다.\n\n" +
				"### 🔎 예시\n" +
				"- 학교 목록: `?councilType=SCHOOL_COUNCIL&page=1&size=20`\n" +
				"- 단과대 카테고리 필터: `?councilType=COLLEGE_COUNCIL&category=PARTNERSHIP&page=1&size=20`\n" +
				"- 상세 하단 다른 글: `?councilType=MAJOR_COUNCIL&excludePostId=123&page=1&size=20`"
	)
	public CommonResponse<Page<PostListItemResponse>> getPosts(
		@RequestParam CouncilType councilType,
		@RequestParam(required = false) PostCategory category,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "20") int size,
		@RequestParam(required = false) Long excludePostId,
		@CurrentUserId Long userId
	) {
		Page<PostListItemResponse> responseDto = postService.findPosts(councilType, category, page, size, userId,
			excludePostId);
		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS, responseDto);
	}

	@GetMapping("/{postId}")
	@Operation(summary = "학생회 게시글 상세 조회")
	public CommonResponse<GetPostForUserResponse> getPost(
		@PathVariable Long postId,
		@CurrentUserId Long userId
	) {
		GetPostForUserResponse responseDto = postService.findById(postId, userId);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_READ_SUCCESS, responseDto);
	}

	@GetMapping("/school/events/upcoming")
	@Operation(
		summary = "학교 범위 72시간 이내 행사 조회",
		description = "사용자의 학교 범위에서 현재 시각 기준 72시간 이내에 시작하는 행사(EVENT) 게시글을 조회합니다. (startDateTime 기준)"
	)
	public CommonResponse<Page<PostListItemResponse>> getUpcomingSchoolEvents(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "3") int size,
		@CurrentUserId Long userId
	) {
		Page<PostListItemResponse> responseDto = postService.findUpcomingSchoolEvents72h(page, size, userId);
		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS, responseDto);
	}

	@GetMapping("/college/events/upcoming")
	@Operation(
		summary = "단과대 범위 72시간 이내 행사 조회",
		description = "사용자의 단과대 범위에서 현재 시각 기준 72시간 이내에 시작하는 행사(EVENT) 게시글을 조회합니다. (startDateTime 기준)"
	)
	public CommonResponse<Page<PostListItemResponse>> getUpcomingCollegeEvents(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "3") int size,
		@CurrentUserId Long userId
	) {
		Page<PostListItemResponse> responseDto = postService.findUpcomingCollegeEvents72h(page, size, userId);
		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS, responseDto);
	}

	@GetMapping("/major/events/upcoming")
	@Operation(
		summary = "학과 범위 72시간 이내 행사 조회",
		description = "사용자의 학과 범위에서 현재 시각 기준 72시간 이내에 시작하는 행사(EVENT) 게시글을 조회합니다. (startDateTime 기준)"
	)
	public CommonResponse<Page<PostListItemResponse>> getUpcomingMajorEvents(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "3") int size,
		@CurrentUserId Long userId
	) {
		Page<PostListItemResponse> responseDto = postService.findUpcomingMajorEvents72h(page, size, userId);
		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS, responseDto);
	}

	@GetMapping("/partnerships/active")
	@Operation(summary = "학생회 타입별 현재 이용 가능한 제휴 (일반 유저 전용 로직, 홈 화면)")
	public CommonResponse<List<GetActivePartnershipListForUserResponse>> getActivePartnershipListForUser(
		@RequestParam CouncilType councilType,
		@CurrentUserId Long userId
	) {
		List<GetActivePartnershipListForUserResponse> responses = postService.findActivePartnershipForUser(councilType,
			userId);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS, responses);
	}

	@GetMapping("/events/today")
	@Operation(summary = "오늘의 행사 조회")
	public CommonResponse<TodayEventResponse> getTodayEvent(@CurrentUserId Long userId) {
		TodayEventResponse response = postService.findTodayEvent(userId);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_READ_SUCCESS, response);
	}
}
