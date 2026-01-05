package com.campus.campus.domain.userpost.presentation;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.councilpost.application.dto.response.PostListItemResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostResponse;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.domain.councilpost.presentation.StudentCouncilPostResponseCode;
import com.campus.campus.domain.userpost.application.service.UserPostService;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users/posts")
@RequiredArgsConstructor
@Slf4j
public class UserPostController {

	private final UserPostService postService;

	@GetMapping("/school")
	@Operation(
		summary = "학교 학생회 게시글 목록 조회",
		description = "현재 사용자가 속한 학교의 학생회 게시글을 조회합니다.\n\n카테고리 필터링이 가능합니다."
	)
	public CommonResponse<Page<PostListItemResponse>> getSchoolPosts(
		@RequestParam(required = false) PostCategory category,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "20") int size,
		@CurrentUserId Long userId
	) {
		return CommonResponse.success(
			StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS,
			postService.findSchoolPosts(category, page, size, userId)
		);
	}

	@GetMapping("/college")
	@Operation(
		summary = "단과대 학생회 게시글 목록 조회",
		description = "현재 사용자가 속한 단과대의 학생회 게시글을 조회합니다.\n\n카테고리 필터링이 가능합니다."
	)
	public CommonResponse<Page<PostListItemResponse>> getCollegePosts(
		@RequestParam(required = false) PostCategory category,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "20") int size,
		@CurrentUserId Long userId
	) {
		return CommonResponse.success(
			StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS,
			postService.findCollegePosts(category, page, size, userId)
		);
	}

	@GetMapping("/major")
	@Operation(
		summary = "전공 학생회 게시글 목록 조회",
		description = "현재 사용자가 속한 전공의 학생회 게시글을 조회합니다.\n\n카테고리 필터링이 가능합니다."
	)
	public CommonResponse<Page<PostListItemResponse>> getMajorPosts(
		@RequestParam(required = false) PostCategory category,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "20") int size,
		@CurrentUserId Long userId
	) {
		return CommonResponse.success(
			StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS,
			postService.findMajorPosts(category, page, size, userId)
		);
	}

	@GetMapping("/{postId}")
	@Operation(summary = "학생회 게시글 상세 조회")
	public CommonResponse<PostResponse> getPost(
		@PathVariable Long postId,
		@CurrentUserId Long userId
	) {
		return CommonResponse.success(
			StudentCouncilPostResponseCode.POST_READ_SUCCESS,
			postService.findById(postId, userId)
		);
	}
}
