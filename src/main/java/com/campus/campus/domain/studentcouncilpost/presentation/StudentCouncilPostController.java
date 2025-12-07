package com.campus.campus.domain.studentcouncilpost.presentation;

import com.campus.campus.domain.studentcouncilpost.application.dto.PostRequestDto;
import com.campus.campus.domain.studentcouncilpost.application.dto.PostResponseDto;
import com.campus.campus.domain.studentcouncilpost.application.service.StudentCouncilPostService;
import com.campus.campus.global.auth.annotation.CurrentUserId;
import com.campus.campus.global.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('COUNCIL')")
@RequestMapping("/student-council/posts")
@RequiredArgsConstructor
public class StudentCouncilPostController {

    private final StudentCouncilPostService postService;

    @PostMapping
    @Operation(summary = "학생회 제휴/행사 게시글 생성")
    public CommonResponse<PostResponseDto> createPost(
            @CurrentUserId Long councilId,
            @RequestBody @Valid PostRequestDto dto
    ) {
        PostResponseDto response = postService.create(councilId, dto);

        return CommonResponse.success(PostResponseCode.POST_CREATE_SUCCESS, response);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "학생회 게시글 단건 조회")
    public CommonResponse<PostResponseDto> getPost(
            @PathVariable Long postId
    ) {
        PostResponseDto response = postService.get(postId);

        return CommonResponse.success(PostResponseCode.POST_READ_SUCCESS, response);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "학생회 게시글 수정")
    public CommonResponse<PostResponseDto> updatePost(
            @CurrentUserId Long councilId,
            @PathVariable Long postId,
            @RequestBody @Valid PostRequestDto dto
    ) {
        PostResponseDto response = postService.update(councilId, postId, dto);

        return CommonResponse.success(PostResponseCode.POST_UPDATE_SUCCESS, response);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "학생회 게시글 삭제")
    public CommonResponse<Void> deletePost(
            @CurrentUserId Long councilId,
            @PathVariable Long postId
    ) {
        postService.delete(councilId, postId);

        return CommonResponse.success(PostResponseCode.POST_DELETE_SUCCESS, null);
    }
}
