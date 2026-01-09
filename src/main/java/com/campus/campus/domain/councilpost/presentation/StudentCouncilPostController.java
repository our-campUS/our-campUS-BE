package com.campus.campus.domain.councilpost.presentation;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campus.campus.domain.councilpost.application.dto.request.PostRequest;
import com.campus.campus.domain.councilpost.application.dto.response.GetPostListForCouncilResponse;
import com.campus.campus.domain.councilpost.application.dto.response.GetUpcomingEventListForCouncilResponse;
import com.campus.campus.domain.councilpost.application.dto.response.PostResponse;
import com.campus.campus.domain.councilpost.application.service.StudentCouncilPostService;
import com.campus.campus.domain.councilpost.domain.entity.PostCategory;
import com.campus.campus.global.annotation.CurrentCouncilId;
import com.campus.campus.global.common.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/student-councils/posts")
@PreAuthorize("hasRole('COUNCIL')")
@Tag(name = "Student Council Post", description = "학생회(COUNCIL) 권한 전용 제휴/행사 게시글 관리 API")
@RequiredArgsConstructor
public class StudentCouncilPostController {

	private final StudentCouncilPostService postService;

	@PostMapping
	@Operation(
		summary = "학생회 제휴/행사 게시글 생성",
		description =
			"새로운 학생회 게시글을 작성합니다.\n\n" +

				"### 📌 핵심 내용\n" +
				"1. **카테고리 선택**\n" +
				"   - `PARTNERSHIP`(제휴) 또는 `EVENT`(행사) 중 하나를 반드시 선택해야 합니다.\n\n" +

				"2. **카테고리별 날짜/시간 규칙**\n" +
				"   - `EVENT` (행사)\n" +
				"     - `startDateTime`은 **필수**입니다. (날짜 + 시간 포함)\n" +
				"     - `endDateTime`은 **허용되지 않습니다**.\n" +
				"   - `PARTNERSHIP` (제휴)\n" +
				"     - `startDateTime`, `endDateTime`은 **모두 필수**입니다.\n" +
				"     - 서버에서 시간은 자동 정규화됩니다.\n\n" +

				"3. **이미지 처리 방식**\n" +
				"   - OCI Presigned URL로 업로드 후 최종 URL 전달\n\n" +

				"4. **권한 제한**\n" +
				"   - 학생회 계정만 작성 가능",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = PostRequest.class),
				examples = {
					@ExampleObject(
						name = "EVENT 게시글",
						summary = "행사 게시글",
						value = """
							{
							  "category": "EVENT",
							  "title": "2025 봄 축제",
							  "content": "중앙 동아리 연합 봄 축제",
							  "place": "대운동장",
							  "startDateTime": "2025-04-10T18:00",
							  "thumbnailIcon": "EVENT",
							  "imageUrls": []
							}
							"""
					),
					@ExampleObject(
						name = "PARTNERSHIP 게시글",
						summary = "제휴 게시글",
						value = """
							{
							  "category": "PARTNERSHIP",
							  "title": "카페 할인",
							  "content": "10% 할인",
							  "place": "OO카페",
							  "startDateTime": "2025-04-01T00:00",
							  "endDateTime": "2025-04-30T23:59",
							  "thumbnailIcon": "CAFE",
							  "imageUrls": []
							}
							"""
					)
				}
			)
		)
	)
	public CommonResponse<PostResponse> createPost(
		@CurrentCouncilId Long councilId,
		@RequestBody @Valid PostRequest requestDto
	) {
		PostResponse responseDto = postService.create(councilId, requestDto);
		return CommonResponse.success(StudentCouncilPostResponseCode.POST_CREATE_SUCCESS, responseDto);
	}

	@PatchMapping("/{postId}")
	@Operation(summary = "학생회 게시글 수정")
	public CommonResponse<PostResponse> updatePost(
		@CurrentCouncilId Long councilId,
		@PathVariable Long postId,
		@RequestBody @Valid PostRequest requestDto
	) {
		PostResponse responseDto = postService.update(councilId, postId, requestDto);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_UPDATE_SUCCESS, responseDto);
	}

	@DeleteMapping("/{postId}")
	@Operation(summary = "학생회 게시글 삭제")
	public CommonResponse<Void> deletePost(@CurrentCouncilId Long councilId, @PathVariable Long postId) {
		postService.delete(councilId, postId);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_DELETE_SUCCESS);
	}

	@GetMapping("/{postId}")
	@Operation(summary = "학생회 게시글 상세 조회")
	public CommonResponse<PostResponse> getPost(@PathVariable Long postId, @CurrentCouncilId Long councilId) {
		PostResponse responseDto = postService.findById(postId, councilId);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_READ_SUCCESS, responseDto);
	}

	@GetMapping
	@Operation(summary = "학생회 제휴/행사 목록 조회 (학생회 홈화면, 로그인한 학생회 작성한 글만)")
	public CommonResponse<Page<GetPostListForCouncilResponse>> getPostListForCouncil(
		@RequestParam(required = false) PostCategory category,
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "3") int size,
		@CurrentCouncilId Long councilId
	) {
		Page<GetPostListForCouncilResponse> response = postService.findPostListForCouncil(councilId,
			category, page, size);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS, response);
	}

	@GetMapping("/events/upcoming")
	@Operation(summary = "학생회 72시간 이내 행사 조회 (학생회 홈화면, 로그인한 학생회 작성한 글만)")
	public CommonResponse<Page<GetUpcomingEventListForCouncilResponse>> getUpcomingEventsForCouncil(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "3") int size,
		@CurrentCouncilId Long councilId
	) {
		Page<GetUpcomingEventListForCouncilResponse> response = postService.findUpcomingEventsForCouncil(councilId,
			page, size);

		return CommonResponse.success(StudentCouncilPostResponseCode.POST_LIST_READ_SUCCESS, response);
	}
}
