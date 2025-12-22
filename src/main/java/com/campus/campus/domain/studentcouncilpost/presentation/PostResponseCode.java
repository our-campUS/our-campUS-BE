package com.campus.campus.domain.studentcouncilpost.presentation;

import com.campus.campus.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PostResponseCode implements ResponseCodeInterface {

    POST_CREATE_SUCCESS(201, HttpStatus.CREATED, "게시글 생성에 성공했습니다."),
    POST_READ_SUCCESS(200, HttpStatus.OK, "게시글 조회에 성공했습니다."),
    POST_LIST_READ_SUCCESS(200, HttpStatus.OK, "게시글 목록 조회에 성공했습니다."),
    POST_UPDATE_SUCCESS(200, HttpStatus.OK, "게시글 수정에 성공했습니다."),
    POST_DELETE_SUCCESS(204, HttpStatus.NO_CONTENT, "게시글 삭제에 성공했습니다."),
    POST_IMAGE_FINALIZE_SUCCESS(200, HttpStatus.OK, "이미지 확정(이동)에 성공했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}