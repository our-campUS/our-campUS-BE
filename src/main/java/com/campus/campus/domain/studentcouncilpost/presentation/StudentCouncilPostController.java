package com.campus.campus.domain.studentcouncilpost.presentation;

import com.campus.campus.domain.studentcouncilpost.application.dto.PostListItemResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostRequestDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.service.StudentCouncilPostService;
import com.campus.campus.domain.studentcouncilpost.domain.entity.PostCategory;
import com.campus.campus.global.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('COUNCIL')")
@RequestMapping("/student-council/posts")
@Tag(name = "Student Council Post", description = "학생회(COUNCIL) 권한 전용 제휴/행사 게시글 관리 API")
@RequiredArgsConstructor
public class StudentCouncilPostController {

    private final StudentCouncilPostService postService;

    @PostMapping
    @Operation(
            summary = "학생회 제휴/행사 게시글 생성",
            description = "새로운 학생회 게시글을 작성합니다. \n\n" +
                    "### [핵심 내용]\n" +
                    "1. **카테고리 선택**: `PARTNERSHIP`(제휴) 또는 `EVENT`(행사) 중 하나를 반드시 선택해야 합니다.\n" +
                    "2. **이미지 처리**: OCI Presigned URL을 통해 먼저 이미지를 업로드한 후, 반환된 **최종 URL**을 `thumbnailImageUrl` 및 `imageUrls` 리스트에 담아 보내야 합니다.\n" +
                    "3. **권한**: 해당 학교/단과대/학과 소속 학생회 계정(`councilId`)만 작성이 가능합니다.\n" +
                    "* `thumbnailImageUrl`이 **없을 경우**, 아래 아이콘 중 하나를 `thumbnailIcon`에 담아 보내야 합니다.\n" +
                        "  - `CAFE`: 카페, 디저트 관련 제휴\n" +
                        "  - `FOOD`: 식당, 술집 등 일반 음식점\n" +
                        "  - `EVENT`: 축제, 공연, 대형 행사\n" +
                        "  - `NOTICE`: 단순 공지사항, 안내\n" +
                        "  - `SPORTS`: 체육대회, 스포츠 시합\n"
    )
    public CommonResponse<PostResponseDto> createPost(
            @CurrentUserId Long councilId,
            @RequestBody @Valid PostRequestDto requestDto
    ) {
        PostResponseDto responseDto = postService.create(councilId, requestDto);
        return CommonResponse.success(PostResponseCode.POST_CREATE_SUCCESS, responseDto);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "학생회 게시글 단건 조회")
    public CommonResponse<PostResponseDto> getPost(
            @PathVariable Long postId,
            @CurrentUserId Long councilId
    ) {
        PostResponseDto responseDto =
                postService.findById(postId, councilId);

        return CommonResponse.success(
                PostResponseCode.POST_READ_SUCCESS,
                responseDto
        );
    }


    @GetMapping
    @Operation(
            summary = "학생회 게시글 목록 조회 (필터링 포함)",
            description = "전체 게시글 혹은 제휴(PARTNERSHIP), 행사(EVENT) 카테고리별로 필터링하여 목록을 조회합니다."
    )
    public CommonResponse<Page<PostListItemResponseDto>> getPostList(
            @Parameter(
                    description = "필터링할 카테고리 (미선택 시 전체 조회)",
                    example = "PARTNERSHIP",
                    schema = @Schema(implementation = PostCategory.class)
            )
            @RequestParam(required = false) PostCategory category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size,
            @CurrentUserId Long councilId
    ) {
        Page<PostListItemResponseDto> responseDto =
                postService.findAll(
                        category, page, size, councilId);

        return CommonResponse.success(
                PostResponseCode.POST_LIST_READ_SUCCESS,
                responseDto
        );
    }


    @PatchMapping("/{postId}")
    @Operation(summary = "학생회 게시글 수정")
    public CommonResponse<PostResponseDto> updatePost(
            @CurrentUserId Long councilId,
            @PathVariable Long postId,
            @RequestBody @Valid PostRequestDto requestDto
    ) {
        PostResponseDto responseDto = postService.update(councilId, postId, requestDto);
        return CommonResponse.success(PostResponseCode.POST_UPDATE_SUCCESS, responseDto);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "학생회 게시글 삭제")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<Void> deletePost(
            @CurrentUserId Long councilId,
            @PathVariable Long postId
    ) {
        postService.delete(councilId, postId);
        return CommonResponse.success(PostResponseCode.POST_DELETE_SUCCESS, null);
    }
}